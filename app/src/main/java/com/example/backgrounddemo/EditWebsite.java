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
import android.content.Intent;
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
import android.widget.Toast;
import com.google.gson.JsonObject;
import org.json.JSONObject;
import java.io.IOException;

public class EditWebsite extends AppCompatActivity {

    EditText urlEditText;
    EditText nameEditText;
    EditText timerEditText;
    EditText totalHitsEditText;

    TextView dailyHitsChangeTextView;
    TextView editWebsiteTextViewMain;

    String category;

    boolean highQuality;

    SeekBar dailyHits;

    int dailyHitsNum;

    int min= 0;

    SharedPreferences sharedPreferences;

    OkHttpClient client;

    String checkToken;

    Switch highQualitySwitch;

    int idToEdit;
    String urlToEdit;
    String nameToEdit;
    int timerToEdit;
    int dailyHitsToEdit;
    int totalHitsToEdit;
    String categoryToEdit;
    Boolean highQualityToEdit;

    RadioButton sRadioButton;
    RadioButton aRadioButton;
    RadioButton pRadioButton;
    RadioButton wsRadioButton;

    public void categorySelect(View view)
    {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId())
        {
            case R.id.editSRadioButton:
                if (checked)
                {
                    category = "S";
                }

                break;
            case R.id.editARadioButton:
                if (checked)
                {
                    category = "A";
                }

                break;

            case R.id.editPRadioButton:
                if (checked)
                {
                    category = "P";
                }

                break;

            case R.id.editWRadioButton:
                if (checked)
                {
                    category = "WS";
                }
                break;

        }
    }

    public void editWebsite(View view)
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

            RequestBody patchReqBody = RequestBody.create(dataToSend.toString(),JSON);

            Log.i("sent data",dataToSend.toString());

            String newToken = "Token "+checkToken;

            Request patchReq = new Request.Builder()
                    .addHeader("Authorization",newToken)
                    .url("https://doorstepdelhi-test.herokuapp.com/webtraffic/websites/"+idToEdit+"/")
                    .put(patchReqBody)
                    .build();

            client.newCall(patchReq).enqueue(new Callback() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_website);

        Intent fromMyWebsites = getIntent();

        idToEdit=fromMyWebsites.getIntExtra("idToEdit",0);
        urlToEdit= fromMyWebsites.getStringExtra("urlToEdit");
        nameToEdit = fromMyWebsites.getStringExtra("nameToEdit");
        timerToEdit = fromMyWebsites.getIntExtra("timerToEdit",0);
        dailyHitsToEdit = fromMyWebsites.getIntExtra("dailyHitsToEdit",0);
        totalHitsToEdit = fromMyWebsites.getIntExtra("totalHitsToEdit",0);
        categoryToEdit=fromMyWebsites.getStringExtra("categoryToEdit");
        highQualityToEdit=fromMyWebsites.getBooleanExtra("highQualityToEdit",false);

        client = new OkHttpClient();

        sharedPreferences = this.getSharedPreferences("com.example.backgrounddemo", Context.MODE_PRIVATE);
        checkToken = sharedPreferences.getString("token", "Error");

        urlEditText = findViewById(R.id.editUrlEditText);
        nameEditText = findViewById(R.id.editNameEditText);
        timerEditText = findViewById(R.id.editTimerEditText);
        totalHitsEditText = findViewById(R.id.editEditTextTotalHits);
        dailyHitsChangeTextView=findViewById(R.id.editDailyHitsChangeTextView);
        highQualitySwitch=findViewById(R.id.editHighQualitySwitch);
        dailyHits =findViewById(R.id.editSeekBarDailyHits);
        editWebsiteTextViewMain=findViewById(R.id.editWebsiteTextViewMain);

        sRadioButton=findViewById(R.id.editSRadioButton);
        aRadioButton=findViewById(R.id.editARadioButton);
        pRadioButton=findViewById(R.id.editPRadioButton);
        wsRadioButton=findViewById(R.id.editWRadioButton);

        urlEditText.setText(urlToEdit.toString());
        nameEditText.setText(nameToEdit.toString());
        timerEditText.setText(Integer.toString(timerToEdit));
        dailyHitsChangeTextView.setText(Integer.toString(dailyHitsToEdit));
        dailyHits.setProgress(dailyHitsToEdit);
        totalHitsEditText.setText(Integer.toString(totalHitsToEdit));

        dailyHitsNum=dailyHitsToEdit;

        if(categoryToEdit.equals("S"))
        {
            sRadioButton.setChecked(true);
            category = "S";
        }

        else if(categoryToEdit.equals("A"))
        {
            aRadioButton.setChecked(true);
            category = "A";
        }

        else if(categoryToEdit.equals("P"))
        {
            pRadioButton.setChecked(true);
            category = "P";
        }
        else
        {
            wsRadioButton.setChecked(true);
            category = "WS";
        }

        editWebsiteTextViewMain.setText("Edit Website:\n"+nameToEdit);

        if(highQualityToEdit)
        {
            highQualitySwitch.setText("True");
            highQualitySwitch.setChecked(true);
            highQuality=true;
        }
        else
        {
            highQualitySwitch.setText("False");
            highQualitySwitch.setChecked(false);
            highQuality=false;
        }

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