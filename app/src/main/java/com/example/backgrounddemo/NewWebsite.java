package com.example.backgrounddemo;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import com.google.gson.JsonObject;
import org.json.JSONObject;
import java.io.IOException;

public class NewWebsite extends AppCompatActivity {

    EditText urlEditText;
    EditText nameEditText;
    EditText timerEditText;
    EditText totalHitsEditText;

    TextView dailyHitsChangeTextView;

    String category;

    boolean highQuality;

    SeekBar dailyHits;

    int dailyHitsNum=30;

    int min= 0;

    SharedPreferences sharedPreferences;

    OkHttpClient client;

    String checkToken;

    Switch highQualitySwitch;

    public void categorySelect(View view)
    {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId())
        {
            case R.id.sRadioButton:
                if (checked)
                {
                    category = "S";
                }

                break;
            case R.id.aRadioButton:
                if (checked)
                {
                    category = "A";
                }

                break;

            case R.id.pRadioButton:
                if (checked)
                {
                    category = "P";
                }

                break;

            case R.id.wRadioButton:
                if (checked)
                {
                    category = "WS";
                }
                break;

        }
    }

    public void submitNewWebsite(View view)
    {
       if(!urlEditText.getText().toString().isEmpty() && !nameEditText.getText().toString().isEmpty() && !timerEditText.getText().toString().isEmpty() && category!=null && !totalHitsEditText.getText().toString().isEmpty())
       {
           JsonObject dataToSend  = new JsonObject();

           dataToSend.addProperty("name",nameEditText.getText().toString());
           dataToSend.addProperty("url",urlEditText.getText().toString());
           dataToSend.addProperty("timer",Integer.parseInt(timerEditText.getText().toString()));
           dataToSend.addProperty("category",category);
           dataToSend.addProperty("daily_hits",dailyHitsNum);
           dataToSend.addProperty("total_hits",Integer.parseInt(totalHitsEditText.getText().toString()));
           dataToSend.addProperty("high_quality",highQuality);
           dataToSend.addProperty("status","I");
           dataToSend.addProperty("traffic_source","D");

           final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

           RequestBody postBody = RequestBody.create(dataToSend.toString(),JSON);

           Log.i("sent data",dataToSend.toString());

           String newToken = "Token "+checkToken;

           Request post = new Request.Builder()
                   .addHeader("Authorization",newToken)
                   //.url("https://808ae607c00d.ngrok.io/webtraffic/websites/")
                   .url("https://doorstepdelhi-test.herokuapp.com/webtraffic/websites/")
                   .post(postBody)
                   .build();

           client.newCall(post).enqueue(new Callback() {
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

                           JSONObject myJsonObject = new JSONObject(responseBody.string());

                           Log.i("hello", myJsonObject+"");

                          // Toast.makeText(NewWebsite.this,"Website added",Toast.LENGTH_LONG).show();

                       }

                   } catch (Exception e) {
                       e.printStackTrace();
                       //Toast.makeText(NewWebsite.this,"Invalid credentials",Toast.LENGTH_LONG).show();
                   }
               }
           });

       }
       else
       {
          // Toast.makeText(NewWebsite.this,"Please enter all the details",Toast.LENGTH_LONG).show();
       }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_website);

        client = new OkHttpClient();

        sharedPreferences = this.getSharedPreferences("com.example.backgrounddemo", Context.MODE_PRIVATE);
        checkToken = sharedPreferences.getString("token", "Error");

        urlEditText = findViewById(R.id.urlEditText);
        nameEditText = findViewById(R.id.nameEditText);
        timerEditText = findViewById(R.id.timerEditText);
        totalHitsEditText = findViewById(R.id.editTextTotalHits);
        dailyHitsChangeTextView=findViewById(R.id.dailyHitsChangeTextView);

        highQualitySwitch=findViewById(R.id.highQualitySwitch);

        highQualitySwitch.setText("False");

        dailyHits =findViewById(R.id.seekBarDailyHits);

        dailyHits.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(progress<min)
                {
                    progress = min;
                    seekBar.setProgress(progress);
                }

                dailyHitsNum = progress;
                dailyHitsChangeTextView.setText(Integer.toString(dailyHitsNum));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        highQualitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                   highQuality=true;
                   highQualitySwitch.setText("True");
                }
                else
                {
                    highQuality=false;
                    highQualitySwitch.setText("False");
                }
            }
        });

    }
}