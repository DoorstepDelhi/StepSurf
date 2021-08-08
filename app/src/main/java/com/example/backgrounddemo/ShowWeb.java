package com.example.backgrounddemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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

public class ShowWeb extends AppCompatActivity {

    public static final String mBroadcastStringAction = "abc";
    public static final String mBroadcastIntegerAction = "ijk";
    public static final String mBroadcastArrayListAction = "xyz";

    public IntentFilter mIntentFilter;

    WebView webView;

    ConstraintLayout showWebLayout;

    boolean surfingInPicture;

    boolean goToBackground=false;

    Button buttonShowWeb;

    static MutableLiveData<Boolean> listen = new MutableLiveData<>();

    boolean serviceIsStopped;

    public void goMain(View view)
    {
        buttonShowWeb.getLayoutParams().height=1;
        buttonShowWeb.setVisibility(View.INVISIBLE);
        buttonShowWeb.setEnabled(false);
        buttonShowWeb.requestLayout();

        Intent goToMainActivity = new Intent(ShowWeb.this,MainActivity.class);
        goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(goToMainActivity);
    }

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(mBroadcastStringAction))
            {
                 webView.loadUrl(intent.getStringExtra("websiteToLoad"));

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
                webView.loadData("<hmtl><body><h1>Hello User!</h1><p>Daily limit reached</p></body></html>","text/html","UTF-8");

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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_web);

        buttonShowWeb=findViewById(R.id.buttonShowWeb);

        showWebLayout=findViewById(R.id.showWebLayout);

        webView = findViewById(R.id.webView);

        listen.observe(ShowWeb.this,new Observer<Boolean>() {
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
            webView.post(new Runnable() {
                @Override
                public void run() {

                    webView.getSettings().setJavaScriptEnabled(true);

                    webView.setWebViewClient(new WebViewClient());

                    mIntentFilter = new IntentFilter();
                    mIntentFilter.addAction(mBroadcastStringAction);
                    mIntentFilter.addAction(mBroadcastIntegerAction);
                    mIntentFilter.addAction(mBroadcastArrayListAction);

                    registerReceiver(mReceiver, mIntentFilter);

                    Intent fromMain = getIntent();

                    if( fromMain.getStringExtra("close").equals("close"))
                    {
                       // Toast.makeText(ShowWeb.this,"not in picture in picture",Toast.LENGTH_LONG).show();

                        surfingInPicture=false;

                        Intent serviceIntent = new Intent(ShowWeb.this, MyService.class);
                        serviceIntent.putExtra("data","start_service_background");
                        startService(serviceIntent);
                    }

                    if( fromMain.getStringExtra("close").equals("picture"))
                    {
                        surfingInPicture=true;

                        Intent serviceIntent = new Intent(ShowWeb.this,MyService.class);
                        serviceIntent.putExtra("data","start_service_screen");
                        startService(serviceIntent);

                        Context context = ShowWeb.this;

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
            Toast.makeText(ShowWeb.this,"err: "+e,Toast.LENGTH_LONG).show();
        }

    }

    /*@Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode)
    {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);

        if (isInPictureInPictureMode)
        {

        }
        else
        {

        }
    }*/

    @Override
    public void onUserLeaveHint ()
    {
        if(MyService.mode.equals("picture"))
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

            Intent goToMainActivity = new Intent(ShowWeb.this,MainActivity.class);
            goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(goToMainActivity);

            super.onBackPressed();
        }
        else
        {
            Toast.makeText(ShowWeb.this,"To go back, first press 'stop' in notification",Toast.LENGTH_LONG).show();
        }

    }

}