package com.example.backgrounddemo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import java.util.ArrayList;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyWebService extends Service {

    CountDownTimer countDownTimer;

    Intent broadcastIntent;

    Intent snoozeIntent;

    PendingIntent snoozePendingIntent;

    String CHANNEL_ID="myChannel2";
    int notificationId=2;

    static boolean myWebServiceIsRunning;

    static String myMode;

    public void createNotificationChannel()
    {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = getString(R.string.channel_name_2);
            String description = getString(R.string.channel_description_2);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    NotificationCompat.Builder myNotification;

    NotificationManagerCompat notificationManager;

    PendingIntent pendingIntent;

    Intent showWebIntent;

    IntentFilter myMIntentFilter2;

    MyShowWeb2 myMReceiver2;

    boolean mySurfingInPicture;

    Handler myHandler;

    static boolean myServiceStopped;

    ArrayList<String> urlsArraylist;
    ArrayList<Integer> timerArrayList;

    int i=0;

    public void showMyWebsites(int j)
    {
        if(i>=urlsArraylist.size())
        {
            i = 0;
            showMyWebsites(i);
        }
            myNotification = new NotificationCompat.Builder(MyWebService.this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                    .setContentTitle("Surfing Websites | Data Used: .")
                    .setContentText(urlsArraylist.get(j))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(false)
                    .addAction(R.drawable.common_google_signin_btn_icon_dark, "Stop",
                            snoozePendingIntent);

            notificationManager.notify(notificationId, myNotification.build());

            broadcastIntent.putExtra("websiteToLoad", urlsArraylist.get(j));

            sendBroadcast(broadcastIntent);

            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    countDownTimer = new CountDownTimer(timerArrayList.get(j) * 1000, 1000) {

                        @Override
                        public void onTick(long l) {
                            Log.i("running", "running");
                        }

                        @Override
                        public void onFinish() {

                            i += 1;
                            showMyWebsites(i);

                        }
                    };

                    countDownTimer.start();
                }

            }, 1000);
    }

    @Override
    public void onCreate()
    {
        myHandler = new Handler(Looper.getMainLooper());

        broadcastIntent = new Intent();

        snoozeIntent = new Intent();

        broadcastIntent.setAction(MyShowWeb.showingMyWebsites);

        snoozeIntent.setAction("stopService");
        snoozeIntent.putExtra("data", "hello");
        snoozePendingIntent = PendingIntent.getBroadcast(MyWebService.this, 0, snoozeIntent, 0);

        myMIntentFilter2 = new IntentFilter();
        myMIntentFilter2.addAction("stopService");
        myMIntentFilter2.addAction("restartService");

        myMReceiver2 = new MyShowWeb2();

        registerReceiver(myMReceiver2,myMIntentFilter2);

        createNotificationChannel();

        showWebIntent = new Intent(this,MyShowWeb.class);
        showWebIntent.putExtra("close", "open");
        pendingIntent = PendingIntent.getActivity(this, 0,showWebIntent, 0);

        myNotification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("Surfing Websites | Data Used: .")
                .setContentText("")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false);

        notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(notificationId, myNotification.build());

        startForeground(notificationId, myNotification.build());
    }

    @Override

    // execution of service will start
    // on calling this method
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getStringExtra("data").equals("stop_service"))
        {
            myWebServiceIsRunning=false;

            myServiceStopped=true;

            myHandler.removeMessages(0);

            myHandler.removeCallbacksAndMessages(null);

            countDownTimer.cancel();

            unregisterReceiver(myMReceiver2);

            stopForeground(true);
            stopSelf();

            MyShowWeb.myListen.setValue(myServiceStopped);

            return START_NOT_STICKY;
        }
        else
        {
            myWebServiceIsRunning = true;

            myServiceStopped=false;

            MyShowWeb.myListen.setValue(myServiceStopped);

            urlsArraylist = (ArrayList<String>)intent.getSerializableExtra("urlsArrayList");
            timerArrayList=  (ArrayList<Integer>)intent.getSerializableExtra("timerArrayList");

            if (intent.getStringExtra("data").equals("start_service_background")) {
                myMode = "background";

                mySurfingInPicture = false;

                showWebIntent = new Intent(this,MyShowWeb.class);
                showWebIntent.putExtra("close", "close");

                pendingIntent = PendingIntent.getActivity(this, 0, showWebIntent, 0);

            }

            if (intent.getStringExtra("data").equals("start_service_screen"))
            {
                myMode = "picture";

                mySurfingInPicture = true;

                showWebIntent = new Intent(this,MyShowWeb.class);
                showWebIntent.putExtra("close", "picture");

                pendingIntent = PendingIntent.getActivity(this, 0, showWebIntent, 0);
            }

            showMyWebsites(i);

            return START_STICKY;
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        Intent restartServiceIntent = new Intent();
        restartServiceIntent.setAction("restartService");

        if(!myMode.equals("picture"))
        {
            restartServiceIntent.putExtra("restartCloseOrPicture", "close");
        }
        else
        {
            restartServiceIntent.putExtra("restartCloseOrPicture", "picture");
        }

        sendBroadcast(restartServiceIntent);

        super.onTaskRemoved(rootIntent);
    }

  /*  @Override

    // execution of the service will
    // stop on calling this method
    public void onDestroy()
    {
        super.onDestroy();

        try
        {
            timer.cancel();
            timer.purge();

            unregisterReceiver(mReceiver2);

            stopForeground(true);
            stopSelf();
        }
        catch(Exception e)
        {

        }
    }*/

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}