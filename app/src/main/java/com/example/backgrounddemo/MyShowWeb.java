package com.example.backgrounddemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import android.app.PictureInPictureParams;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MyShowWeb extends AppCompatActivity {

    public static final String showingMyWebsites = "abc";
    public static final String mBroadcastIntegerAction = "ijk";
    public static final String mBroadcastArrayListAction = "xyz";

    public IntentFilter myMIntentFilter;

    WebView myWebView;

    boolean surfingInPicture;

    boolean goToBackground=false;

    Button buttonShowWeb;

    static MutableLiveData<Boolean> myListen = new MutableLiveData<>();

    boolean serviceIsStopped;

    ArrayList<String> urlsArraylist;
    ArrayList<Integer> timerArrayList;

    public void goMain(View view)
    {
        buttonShowWeb.getLayoutParams().height=1;
        buttonShowWeb.setVisibility(View.INVISIBLE);
        buttonShowWeb.setEnabled(false);
        buttonShowWeb.requestLayout();

        Intent goToMainActivity = new Intent(MyShowWeb.this,MainActivity.class);
        goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(goToMainActivity);
    }

    public BroadcastReceiver myMReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(showingMyWebsites))
            {
                myWebView.loadUrl(intent.getStringExtra("websiteToLoad"));

                if(!surfingInPicture && !goToBackground)
                {
                    goToBackground=true;
                    Intent closeAppIntent = new Intent();
                    closeAppIntent.setAction(Intent.ACTION_MAIN);
                    closeAppIntent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(closeAppIntent);
                }
            }
            else if (intent.getAction().equals(mBroadcastIntegerAction))
            {
                myWebView.loadData("<hmtl><body><h1>Hello User!</h1><p>Daily limit reached</p></body></html>","text/html","UTF-8");

                if(!surfingInPicture && !goToBackground)
                {
                    goToBackground=true;
                    Intent closeAppIntent = new Intent();
                    closeAppIntent.setAction(Intent.ACTION_MAIN);
                    closeAppIntent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(closeAppIntent);
                }
            }
            else if (intent.getAction().equals(mBroadcastArrayListAction))
            {

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_show_web);

        buttonShowWeb=findViewById(R.id.myButtonShowWeb);

        myWebView = findViewById(R.id.myWebView);

        myListen.observe(MyShowWeb.this,new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean changedValue) {

                serviceIsStopped = changedValue;

                if(changedValue)
                {
                    buttonShowWeb.setVisibility(View.VISIBLE);
                    buttonShowWeb.setText("EXIT");
                    buttonShowWeb.getLayoutParams().height=100;
                    buttonShowWeb.setEnabled(true);

                    buttonShowWeb.requestLayout();
                }
                else
                {
                    buttonShowWeb.getLayoutParams().height=1;
                    buttonShowWeb.setVisibility(View.INVISIBLE);
                    buttonShowWeb.setEnabled(false);
                    buttonShowWeb.requestLayout();
                }
            }
        });

        try
        {
            myWebView.post(new Runnable() {
                @Override
                public void run() {

                    myWebView.getSettings().setJavaScriptEnabled(true);

                    myWebView.setWebViewClient(new WebViewClient());

                    myMIntentFilter = new IntentFilter();
                    myMIntentFilter.addAction(showingMyWebsites);
                    myMIntentFilter.addAction(mBroadcastIntegerAction);
                    myMIntentFilter.addAction(mBroadcastArrayListAction);

                    registerReceiver(myMReceiver,myMIntentFilter);

                    Intent fromMain = getIntent();

                    urlsArraylist = (ArrayList<String>)getIntent().getSerializableExtra("urlsArrayList");
                    timerArrayList=  (ArrayList<Integer>)getIntent().getSerializableExtra("timerArrayList");

                    if( fromMain.getStringExtra("close").equals("close"))
                    {
                        // Toast.makeText(ShowWeb.this,"not in picture in picture",Toast.LENGTH_LONG).show();

                        surfingInPicture=false;

                        Intent serviceIntent = new Intent(MyShowWeb.this,MyWebService.class);
                        serviceIntent.putExtra("data","start_service_background");
                        serviceIntent.putExtra("urlsArrayList",urlsArraylist);
                        serviceIntent.putExtra("timerArrayList",timerArrayList);
                        startService(serviceIntent);
                    }

                    if( fromMain.getStringExtra("close").equals("picture"))
                    {
                        surfingInPicture=true;

                        Intent serviceIntent = new Intent(MyShowWeb.this,MyWebService.class);
                        serviceIntent.putExtra("data","start_service_screen");
                        serviceIntent.putExtra("urlsArrayList",urlsArraylist);
                        serviceIntent.putExtra("timerArrayList",timerArrayList);
                        startService(serviceIntent);

                        Context context = MyShowWeb.this;

                        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE))
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                            {
                                PictureInPictureParams params = new PictureInPictureParams.Builder()
                                        .setAspectRatio(MainActivity.ratio)
                                        //.setActions(actions)
                                        .build();

                                //setPictureInPictureParams(params);

                                enterPictureInPictureMode(params);
                            }
                        }
                    }

                    if(fromMain.getStringExtra("close").equals("pictureResume"))
                    {
                        surfingInPicture=true;
                    }

                }
            });

        }
        catch(Exception e)
        {
            Toast.makeText(MyShowWeb.this,"err: "+e,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUserLeaveHint ()
    {
        if(MyWebService.myMode.equals("picture"))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                PictureInPictureParams params = new PictureInPictureParams.Builder()
                        .setAspectRatio(MainActivity.ratio)
                        //.setActions(actions)
                        .build();

                enterPictureInPictureMode(params);
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        if(serviceIsStopped)
        {
            buttonShowWeb.getLayoutParams().height=1;
            buttonShowWeb.setVisibility(View.INVISIBLE);
            buttonShowWeb.setEnabled(false);
            buttonShowWeb.requestLayout();

            Intent goToMainActivity = new Intent(MyShowWeb.this,MainActivity.class);
            goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(goToMainActivity);

            super.onBackPressed();
        }
        else
        {
            Toast.makeText(MyShowWeb.this,"To go back, first press 'stop' in notification",Toast.LENGTH_LONG).show();
        }

    }

}