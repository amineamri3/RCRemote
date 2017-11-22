package com.github.niqdev.ipcam;


import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


/**
 * Created by Amine on 29/10/2017.
 */

public class ConnectionHelper extends AsyncTask<String ,Void ,Void> {

Socket socket;


    @Override
    protected Void doInBackground(String... params) {

        try {
            Log.d("IP",params[0]);
            socket = new Socket(params[0], 8888);
            DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());

            DOS.writeBytes(params[1]+'\n');
            Log.d("SENT",params[1]);
            DOS.flush();
            DOS.close();
            socket.close();



        } catch (Exception e) {
            e.printStackTrace();
        }
return null;
    }

    @Override
    protected void onProgressUpdate(Void... params) {

    }

}