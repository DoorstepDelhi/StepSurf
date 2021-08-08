package com.example.backgrounddemo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import java.util.Map;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class MyService extends Service{

    SharedPreferences sharedPreferences;

    OkHttpClient client;

    CountDownTimer countDownTimer;

    Map jsonJavaRootObject;

    Intent broadcastIntent;

    Intent snoozeIntent;

    PendingIntent snoozePendingIntent;

    long y;
    long idLong;

    String checkToken;

    WebSocket ws;

    final class EchoWebSocketListener extends WebSocketListener {
        //private static final int NORMAL_CLOSURE_STATUS = 1000;

        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            Log.i("xyz", "xyz " + response);
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {

            if (!text.equals("null"))
            {

            Log.i("text1 is", text);
            Log.i("text2 is", text);
            Log.i("text3 is", text);
            Log.i("text4 is", text);
            Log.i("text5 is", text);

            jsonJavaRootObject = new Gson().fromJson(text, Map.class);

            Log.i("timer is: ", jsonJavaRootObject.get("timer").toString());
            Log.i("url is: ", jsonJavaRootObject.get("url").toString());

            String time = jsonJavaRootObject.get("timer").toString();
            String urlToLoad = jsonJavaRootObject.get("url").toString();
            String id = jsonJavaRootObject.get("id").toString();

            Log.i("timer2 is: ", time);
            Log.i("url2 is: ", urlToLoad);
            Log.i("url3 is: ", urlToLoad);
            Log.i("url4 is: ", urlToLoad);

            Log.i("hi", "hi");

            Log.i("abc", "abc");

            myNotification = new NotificationCompat.Builder(MyService.this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                    .setContentTitle("Surfing Websites | Data Used: .")
                    .setContentText(urlToLoad)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(false)
                    .addAction(R.drawable.common_google_signin_btn_icon_dark, "Stop",
                            snoozePendingIntent);

            notificationManager.notify(notificationId,myNotification.build());

            broadcastIntent.putExtra("websiteToLoad",urlToLoad);

            sendBroadcast(broadcastIntent);

            Log.i("pqr", "pqr");

            Double x = Double.parseDouble(time.trim());
            y = x.longValue();

            Double idDouble = Double.parseDouble(id.trim());
            idLong = idDouble.longValue();

            Log.i("x is:", x + "");
            Log.i("y is:", y + "");
            Log.i("y+1 is:", y + 1 + "");

            Log.i("mno", "mno");

            Log.i("ghi", "ghi");

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    countDownTimer = new CountDownTimer(y * 1000, 1000) {

                        @Override
                        public void onTick(long l) {
                            Log.i("running", "running");
                        }

                        @Override
                        public void onFinish() {

                            JSONObject dataToSend = new JSONObject();

                            try {
                                dataToSend.put("website", idLong);

                                if (!mode.equals("picture"))
                                {
                                    dataToSend.put("type", "B");
                                }
                                else
                                {
                                    dataToSend.put("type", "O");
                                }

                            } catch (Exception e) {

                            }

                            webSocket.send(dataToSend.toString());

                        }
                    };

                    countDownTimer.start();
                }

            }, 1000);

        }
        else
        {
            myNotification = new NotificationCompat.Builder(MyService.this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                    .setContentTitle("Surfing Websites | Data Used: .")
                    .setContentText("Daily limit reached")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(false)
                    .addAction(R.drawable.common_google_signin_btn_icon_dark, "Stop",
                            snoozePendingIntent);

            notificationManager.notify(notificationId,myNotification.build());

            broadcastIntent.setAction(ShowWeb.mBroadcastIntegerAction);

            sendBroadcast(broadcastIntent);
        }
     }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            webSocket.close(1000, null);
        }
    }

    String CHANNEL_ID="myChannel";
    int notificationId=1;

    static boolean serviceIsRunning;

    static String mode;

    public void createNotificationChannel()
    {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
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

    IntentFilter mIntentFilter2;

    ShowWeb2 mReceiver2;

    boolean surfingInPicture;

    Handler handler;

    static boolean serviceStopped;

    @Override
    public void onCreate()
    {
        sharedPreferences = this.getSharedPreferences("com.example.backgrounddemo", Context.MODE_PRIVATE);
        checkToken = sharedPreferences.getString("token", "Error");

        Log.i("token service is:",checkToken);

        handler = new Handler(Looper.getMainLooper());

        try
        {
            JSONObject dataToSend = new JSONObject();
            dataToSend.put("website",7);
            dataToSend.put("type","B");

            Log.i("dataToSend",dataToSend.toString());
        }
        catch(Exception e)
        {

        }

        broadcastIntent = new Intent();

        snoozeIntent = new Intent();

        broadcastIntent.setAction(ShowWeb.mBroadcastStringAction);

        snoozeIntent.setAction("stopService");
        snoozeIntent.putExtra("data", "hello");
        snoozePendingIntent = PendingIntent.getBroadcast(MyService.this, 0, snoozeIntent, 0);

        mIntentFilter2 = new IntentFilter();
        mIntentFilter2.addAction("stopService");
        mIntentFilter2.addAction("restartService");

        mReceiver2 = new ShowWeb2();

        registerReceiver(mReceiver2, mIntentFilter2);

        createNotificationChannel();

        showWebIntent = new Intent(this, ShowWeb.class);
        showWebIntent.putExtra("close", "open");
        //showWebIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
            serviceIsRunning=false;

            serviceStopped=true;

            countDownTimer.cancel();

            handler.removeMessages(0);

            handler.removeCallbacksAndMessages(null);

            unregisterReceiver(mReceiver2);

            client.dispatcher().executorService().shutdown();

            ws.close(1000, "Goodbye!");

            stopForeground(true);
            stopSelf();

            ShowWeb.listen.setValue(serviceStopped);

            return START_NOT_STICKY;
        }
        else
            {
                serviceIsRunning = true;

                serviceStopped=false;

                ShowWeb.listen.setValue(serviceStopped);

            if (intent.getStringExtra("data").equals("start_service_background")) {
                mode = "background";

                surfingInPicture = false;

                showWebIntent = new Intent(this, ShowWeb.class);
                showWebIntent.putExtra("close", "close");

                pendingIntent = PendingIntent.getActivity(this, 0, showWebIntent, 0);

            }

            if (intent.getStringExtra("data").equals("start_service_screen"))
            {
                mode = "picture";

                surfingInPicture = true;

                showWebIntent = new Intent(this, ShowWeb.class);
                showWebIntent.putExtra("close", "picture");

                pendingIntent = PendingIntent.getActivity(this, 0, showWebIntent, 0);
            }

            client = new OkHttpClient();

            String url = "wss://doorstepdelhi-test.herokuapp.com/ws/websites/?token="+checkToken;

            Request request = new Request.Builder().url(url.toString()).build();
            EchoWebSocketListener listener = new EchoWebSocketListener();
            ws = client.newWebSocket(request, listener);

            return START_STICKY;
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        Intent restartServiceIntent = new Intent();
        restartServiceIntent.setAction("restartService");

        if(!mode.equals("picture"))
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