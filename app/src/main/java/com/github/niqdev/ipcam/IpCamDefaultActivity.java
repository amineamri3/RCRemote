package com.github.niqdev.ipcam;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.github.niqdev.ipcam.settings.SettingsActivity.PREF_AUTH_PASSWORD;
import static com.github.niqdev.ipcam.settings.SettingsActivity.PREF_AUTH_USERNAME;
import static com.github.niqdev.ipcam.settings.SettingsActivity.PREF_IPCAM_URL;

public class IpCamDefaultActivity extends AppCompatActivity {

    private static final int TIMEOUT = 5;
    Button btn_U,btn_D,btn_L,btn_R;
    String full_adress;
    String ip_Adress;
    boolean isUpPressed , isDownPressed;

    @BindView(R.id.mjpegViewDefault)
    MjpegView mjpegView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipcam_default);
        getSupportActionBar().setTitle("Car Remote");
        btn_U = (Button)findViewById(R.id.button_U);
        btn_D = (Button)findViewById(R.id.button_D);
        btn_L = (Button)findViewById(R.id.button_L);
        btn_R = (Button)findViewById(R.id.button_R);
        ButterKnife.bind(this);
        full_adress = getPreference(PREF_IPCAM_URL);
        Log.d("IP",full_adress);
        ip_Adress = full_adress.replaceAll(".*//","");
        Log.d("IP",ip_Adress);
        ip_Adress = ip_Adress.replaceAll(":.*","");
        Log.d("IP",ip_Adress);
        isUpPressed = false;
        isDownPressed = false;
        btn_U.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isUpPressed){
                    isUpPressed=true;
                    if(isDownPressed){
                        new ConnectionHelper().execute(ip_Adress,"D0");
                        isDownPressed = false;
                        btn_D.getBackground().setColorFilter(null);
                    }
                    btn_U.getBackground().setColorFilter(new LightingColorFilter(0xff888888, 0x000000));
                    new ConnectionHelper().execute(ip_Adress,"U1");

                }else{
                    isUpPressed = false;
                    new ConnectionHelper().execute(ip_Adress,"U0");
                    btn_U.getBackground().setColorFilter(null);

                }
            }
        });

        btn_D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDownPressed){
                    isDownPressed=true;
                    if(isUpPressed){
                        new ConnectionHelper().execute(ip_Adress,"U0");
                        isUpPressed = false;
                        btn_U.getBackground().setColorFilter(null);
                    }
                    btn_D.getBackground().setColorFilter(new LightingColorFilter(0xff888888, 0x000000));
                    new ConnectionHelper().execute(ip_Adress,"D1");

                }else{
                    isDownPressed = false;
                    new ConnectionHelper().execute(ip_Adress,"D0");
                    btn_D.getBackground().setColorFilter(null);
                }
            }
        });



        btn_R.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN)
                    new ConnectionHelper().execute(ip_Adress,"R1");
                else
                    new ConnectionHelper().execute(ip_Adress,"R1");
                    return false;
            }
        });

        btn_L.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN)
                    new ConnectionHelper().execute(ip_Adress,"L1");
                else
                    new ConnectionHelper().execute(ip_Adress,"L1");
                    return false;
            }
        });
    }

    private String getPreference(String key) {
        return PreferenceManager
            .getDefaultSharedPreferences(this)
            .getString(key, "");
    }

    private DisplayMode calculateDisplayMode() {
        int orientation = getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_LANDSCAPE ?
            DisplayMode.FULLSCREEN : DisplayMode.BEST_FIT;
    }

    private void loadIpCam() {
        Mjpeg.newInstance()
            .credential(getPreference(PREF_AUTH_USERNAME), getPreference(PREF_AUTH_PASSWORD))
            .open(getPreference(PREF_IPCAM_URL), TIMEOUT)
            .subscribe(
                inputStream -> {
                    mjpegView.setSource(inputStream);
                    mjpegView.setDisplayMode(calculateDisplayMode());
                    mjpegView.showFps(true);
                },
                throwable -> {
                    Log.e(getClass().getSimpleName(), "mjpeg error", throwable);
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadIpCam();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mjpegView.stopPlayback();
    }

}
