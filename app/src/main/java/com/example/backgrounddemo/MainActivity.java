package com.example.backgrounddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    OkHttpClient client;

    Button button1;
    Button button2;
    Button button3;
    Button button4;

    int width1;
    int width2;
    int width3;
    int width4;

    int width;
    int height;

    ConstraintLayout constraintLayout;

    boolean foundWidthAndHeight=false;

    static Rational ratio;

    SharedPreferences sharedPreferences;

    String checkToken;

    String newToken;

    ArrayList<String> nameArrayList;
    ArrayList<String> hitsArrayList;
    ArrayList<String> urlArrayList;
    ArrayList<Integer> idArrayList;
    ArrayList<String> categoryArrayList;
    ArrayList<Integer> dailyhitsArrayList;
    ArrayList<Integer> totalhitsArrayList;
    ArrayList<Integer> timerArrayList;
    ArrayList<Boolean> highQualityArrayList;
    ArrayList<Boolean> clickedArrayList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //this is like a global variable being declared to be able to use
        //menu bar anywhere in program

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.menu_bar,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //when an item of menubar is selected
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {


            case R.id.newWebsite:

                Intent goToNewWebsite = new Intent(getApplicationContext(),NewWebsite.class);
                startActivity(goToNewWebsite);

                return true;

            case R.id.myWebsites:

                Request getReq = new Request.Builder()
                        .addHeader("Authorization",newToken)
                        .url("https://doorstepdelhi-test.herokuapp.com/webtraffic/websites/")
                        .get()
                        .build();

                client.newCall(getReq).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        try {

                            if (!response.isSuccessful())
                            {
                                throw new IOException("Unexpected code " + response);
                            }

                            else
                            {

                                ResponseBody responseBody = response.body();

                                JSONArray myJsonArray = new JSONArray(responseBody.string());

                                Log.i("hello", myJsonArray+"");

                                for(int i=0;i<myJsonArray.length();i+=1)
                                {
                                    JSONObject myJsonObject = new JSONObject(myJsonArray.get(i).toString());
                                    Log.i("hello 2", myJsonObject+"");

                                    nameArrayList.add(myJsonObject.getString("name"));
                                    hitsArrayList.add("1");
                                    clickedArrayList.add(false);
                                    urlArrayList.add(myJsonObject.getString("url"));
                                    dailyhitsArrayList.add(myJsonObject.getInt("daily_hits"));
                                    totalhitsArrayList.add(myJsonObject.getInt("total_hits"));
                                    highQualityArrayList.add(myJsonObject.getBoolean("high_quality"));
                                    idArrayList.add(myJsonObject.getInt("id"));
                                    timerArrayList.add(myJsonObject.getInt("timer"));
                                    categoryArrayList.add(myJsonObject.getString("category"));
                                }

                                Log.i("nameList",nameArrayList.toString());

                                Intent goToMyWebsites = new Intent(getApplicationContext(),MyWebsites.class);
                                goToMyWebsites.putExtra("websiteNames",nameArrayList);
                                goToMyWebsites.putExtra("websiteHits",hitsArrayList);
                                goToMyWebsites.putExtra("websiteChecked",clickedArrayList);
                                goToMyWebsites.putExtra("websiteUrls",urlArrayList);
                                goToMyWebsites.putExtra("websiteDailyHits",dailyhitsArrayList);
                                goToMyWebsites.putExtra("websiteTotalHits",totalhitsArrayList);
                                goToMyWebsites.putExtra("websiteHighQuality",highQualityArrayList);
                                goToMyWebsites.putExtra("websiteIds",idArrayList);
                                goToMyWebsites.putExtra("websiteTimers",timerArrayList);
                                goToMyWebsites.putExtra("websiteCategories",categoryArrayList);
                                startActivity(goToMyWebsites);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            //Toast.makeText(NewWebsite.this,"Invalid credentials",Toast.LENGTH_LONG).show();
                        }
                    }
                });

                return true;

            default:
                return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {

               Intent serviceIntent = new Intent(this, MyService.class);
               startService(serviceIntent);

            }
        }
    }

    public void start(View view)
    {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED)
        { //checking if permission for gallery has been granted or not
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.FOREGROUND_SERVICE}, 1);
        }
        else
        {
            MyService.serviceStopped=false;

            Intent goToShowWeb = new Intent(getApplicationContext(),ShowWeb.class);
            goToShowWeb.putExtra("close", "close");
            goToShowWeb.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(goToShowWeb);

            /*if ("xiaomi".equalsIgnoreCase(android.os.Build.MANUFACTURER))
            {
                Intent autostartIntent = new Intent();
                autostartIntent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                startActivity(autostartIntent);

                countDownTimer = new CountDownTimer(1500, 1000) {

                    @Override
                    public void onTick(long l) { //l is no of milliseconds left in timer

                    }

                    @Override
                    public void onFinish() {

                        Intent goToShowWeb = new Intent(getApplicationContext(), ShowWeb.class);
                        goToShowWeb.putExtra("close", "close");
                        startActivity(goToShowWeb);
                    }
                }.start();
            }
            else
            {*/

          //  }

        }
    }

    public void surfScreen(View view)
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) { //checking if permission for gallery has been granted or not
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.FOREGROUND_SERVICE}, 1);
        } else {
            if (foundWidthAndHeight) {
                Context context = MainActivity.this;

                if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE))
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
                        ratio = new Rational(width, height);

                    }

                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    ActivityManager am = (ActivityManager) getSystemService(MainActivity.ACTIVITY_SERVICE);

                    if (am != null)
                    {
                        List<ActivityManager.AppTask> tasks = am.getAppTasks();
                        if (tasks != null && tasks.size() > 0) {
                            tasks.get(0).setExcludeFromRecents(true);
                        }
                    }
                }

                MyService.serviceStopped=false;

                Intent goToShowWeb = new Intent(getApplicationContext(), ShowWeb.class);
                goToShowWeb.putExtra("close", "picture");
                goToShowWeb.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goToShowWeb);
            }
        }
    }

    public void desktopApplication(View view)
    {
        Uri uri = Uri.parse("https://www.google.com/");
        Intent intent= new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);
    }

    /*public void stop(View view)
    {
        Intent stopIntent = new Intent(MainActivity.this, MyService.class);
        stopService(stopIntent);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("com.example.backgrounddemo", Context.MODE_PRIVATE);
        checkToken = sharedPreferences.getString("token", "Error");

        if(checkToken.equals("Error"))
        {
            Intent LogInActivity = new Intent(getApplicationContext(),LogIn.class);
            LogInActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(LogInActivity);
        }

        client = new OkHttpClient();

        newToken = "Token "+checkToken;

        nameArrayList=new ArrayList<String>();
        hitsArrayList = new ArrayList<String>();
        idArrayList = new ArrayList<Integer>();
        categoryArrayList = new ArrayList<String>();
        clickedArrayList = new ArrayList<Boolean>();
        urlArrayList = new ArrayList<String>();
        dailyhitsArrayList = new ArrayList<Integer>();
        totalhitsArrayList = new ArrayList<Integer>();
        timerArrayList = new ArrayList<Integer>();
        highQualityArrayList = new ArrayList<Boolean>();

        button1 = findViewById(R.id.button1a);
        button2 = findViewById(R.id.button2b);
        button3 = findViewById(R.id.button3c);
        button4 = findViewById(R.id.button4d);

        constraintLayout = findViewById(R.id.constraintLayoutMain);

        constraintLayout.post(new Runnable() {
            @Override
            public void run() {

                width = (int) constraintLayout.getWidth();
                height = (int) constraintLayout.getHeight();

                foundWidthAndHeight = true;
            }
        });

        button1.post(new Runnable() {
            @Override
            public void run() {

                width1 = (int) button1.getWidth();

                button1.getLayoutParams().height = width1;

                button1.requestLayout();

                button1.setVisibility(View.VISIBLE);
            }
        });

        button2.post(new Runnable() {
            @Override
            public void run() {

                width2 = (int) button2.getWidth();

                button2.getLayoutParams().height = width2;

                button2.requestLayout();

                button2.setVisibility(View.VISIBLE);

            }
        });

        button3.post(new Runnable() {
            @Override
            public void run() {

                width3 = (int) button3.getWidth();

                button3.getLayoutParams().height = width3;

                button3.requestLayout();

                button3.setVisibility(View.VISIBLE);
            }
        });

        button4.post(new Runnable() {
            @Override
            public void run() {

                width4 = (int) button4.getWidth();

                button4.getLayoutParams().height = width4;

                button4.requestLayout();

                button4.setVisibility(View.VISIBLE);
            }
        });

    }

    /*@Override
    public void onResume()
    {
        super.onResume();

        //Toast.makeText(MainActivity.this,"def",Toast.LENGTH_LONG).show();

        if(MyService.serviceIsRunning)
        {
            constraintLayout.setVisibility(View.INVISIBLE);
            //Toast.makeText(MainActivity.this,"Service Is Running",Toast.LENGTH_LONG).show();

            Intent goToShowWeb = new Intent(getApplicationContext(),ShowWeb.class);

            if(MyService.mode.equals("picture"))
            {
                goToShowWeb.putExtra("close", "pictureResume");
            }

            startActivity(goToShowWeb);
        }

    }*/

}