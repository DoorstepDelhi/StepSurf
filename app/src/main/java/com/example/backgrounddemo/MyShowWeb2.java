package com.example.backgrounddemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MyShowWeb2 extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent.getAction().equals("stopService"))
        {
            Toast.makeText(context, "Stopping step surf.", Toast.LENGTH_LONG).show();

            Intent serviceIntent = new Intent(context, MyWebService.class);
            serviceIntent.putExtra("data","stop_service");
            context.startService(serviceIntent);

          /* Intent stopIntent = new Intent(context, MyService.class);
            context.stopService(stopIntent);*/

            // android.os.Process.killProcess(android.os.Process.myPid());
        }

        if(intent.getAction().equals("restartService"))
        {
            Intent serviceIntent = new Intent(context, MyWebService.class);

            if (intent.getStringExtra("restartCloseOrPicture").equals("close"))
            {
                serviceIntent.putExtra("data","start_service_background");
            }

            if (intent.getStringExtra("restartCloseOrPicture").equals("picture"))
            {
                serviceIntent.putExtra("data","start_service_screen");
            }

            context.startService(serviceIntent);
        }

    }
}