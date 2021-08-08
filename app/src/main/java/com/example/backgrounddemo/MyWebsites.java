package com.example.backgrounddemo;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import java.util.ArrayList;

public class MyWebsites extends AppCompatActivity {

    public void AddNewWebsite(View view)
    {
        Intent goToNewWebsite = new Intent(getApplicationContext(),NewWebsite.class);
        startActivity(goToNewWebsite);
    }

    public void surfMyWebsite(View view)
    {
        Intent goToMyShowWeb = new Intent(getApplicationContext(),MyShowWeb.class);
        goToMyShowWeb.putExtra("close", "close");
        goToMyShowWeb.putExtra("urlsArrayList",urlsArraylist);
        goToMyShowWeb.putExtra("timerArrayList",timerArrayList);
        goToMyShowWeb.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(goToMyShowWeb);
    }

    LinearLayout myWebsiteLinearLayout;

    OkHttpClient client;

    ArrayList<String> nameArrayList;
    ArrayList<String> hitsArrayList;
    ArrayList<Integer> idsArrayList;
    ArrayList<Boolean> clickedArrayList;
    ArrayList<String> urlsArraylist;
    ArrayList<String> categoriesArrayList;
    ArrayList<Integer> dailyHitsArrayList;
    ArrayList<Integer> totalHitsArrayList;
    ArrayList<Integer> timerArrayList;
    ArrayList<Boolean> highQualityArrayList;

    SharedPreferences sharedPreferences;

    String checkToken;

    String newToken;

    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_websites);

        nameArrayList=(ArrayList<String>)getIntent().getSerializableExtra("websiteNames");
        hitsArrayList=(ArrayList<String>)getIntent().getSerializableExtra("websiteHits");
        clickedArrayList = (ArrayList<Boolean>)getIntent().getSerializableExtra("websiteChecked");
        urlsArraylist = (ArrayList<String>)getIntent().getSerializableExtra("websiteUrls");
        dailyHitsArrayList=(ArrayList<Integer>)getIntent().getSerializableExtra("websiteDailyHits");
        totalHitsArrayList = (ArrayList<Integer>)getIntent().getSerializableExtra("websiteTotalHits");
        highQualityArrayList = (ArrayList<Boolean>)getIntent().getSerializableExtra("websiteHighQuality");
        idsArrayList=(ArrayList<Integer>)getIntent().getSerializableExtra("websiteIds");
        timerArrayList = (ArrayList<Integer>)getIntent().getSerializableExtra("websiteTimers");
        categoriesArrayList=(ArrayList<String>)getIntent().getSerializableExtra("websiteCategories");

        myWebsiteLinearLayout=findViewById(R.id.myWebsiteLinearLayout);

        Log.i("the arraylist is",nameArrayList.toString());

        client = new OkHttpClient();

        sharedPreferences = this.getSharedPreferences("com.example.backgrounddemo", Context.MODE_PRIVATE);
        checkToken = sharedPreferences.getString("token", "Error");

        newToken= "Token "+checkToken;

        for(i=0;i<nameArrayList.size();i+=1)
        {
            LinearLayout websiteDetailsLinearLayout =  new LinearLayout(getApplicationContext());

            websiteDetailsLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams websiteDetailsLinearLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            websiteDetailsLinearLayout.setWeightSum(3f);
            websiteDetailsLinearLayout.setTag(i);

            myWebsiteLinearLayout.addView(websiteDetailsLinearLayout,websiteDetailsLinearLayoutParams);

            LinearLayout spaceLine=new LinearLayout(getApplicationContext());
            spaceLine.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams spaceLineLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
            );

            spaceLine.setBackgroundColor(getResources().getColor(R.color.black));

            myWebsiteLinearLayout.addView(spaceLine,spaceLineLayoutParams);

            Space space1=new Space(getApplicationContext());

            LinearLayout.LayoutParams space1LayoutParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.1f
            );

            websiteDetailsLinearLayout.addView(space1,space1LayoutParams);

            TextView websiteDetails = new TextView(getApplicationContext());

            LinearLayout.LayoutParams websiteDetailsLayoutParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.8f
                    );

            websiteDetailsLayoutParams.setMargins( 0 , 10, 0 , 10 ) ;
            websiteDetailsLayoutParams.gravity = Gravity.LEFT|Gravity.CENTER_VERTICAL;

            websiteDetails.setText(nameArrayList.get(i));
            websiteDetails.setTextSize(20);
            websiteDetails.setTextColor(getResources().getColor(R.color.black));

            websiteDetails.setId(i);

            websiteDetailsLinearLayout.addView(websiteDetails,websiteDetailsLayoutParams);

            TextView hitsTextView = new TextView(getApplicationContext());

            LinearLayout.LayoutParams hitsTextViewLayoutParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.4f
            );

            hitsTextView.setText("1");
            hitsTextView.setTextSize(20);
            hitsTextView.setTextColor(getResources().getColor(R.color.black));

            hitsTextViewLayoutParams.gravity=Gravity.CENTER;

            websiteDetailsLinearLayout.addView(hitsTextView,hitsTextViewLayoutParams);

            LinearLayout statusLinearLayout = new LinearLayout(getApplicationContext());
            statusLinearLayout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams statusLinearLayoutParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.7f
            );

            statusLinearLayoutParams.setMargins( 0 , 10, 0 , 10 );

            statusLinearLayoutParams.gravity=Gravity.LEFT|Gravity.CENTER_VERTICAL;

            websiteDetailsLinearLayout.addView(statusLinearLayout,statusLinearLayoutParams);

            TextView statusTextView = new TextView(getApplicationContext());

            LinearLayout.LayoutParams statusTextViewLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            statusTextView.setText("Status");
            statusTextView.setGravity(Gravity.CENTER);
            statusTextView.setTextSize(20);
            statusTextView.setTextColor(getResources().getColor(R.color.white));
            statusTextView.setBackgroundResource(R.drawable.roundbuttons);

            statusLinearLayout.addView(statusTextView,statusTextViewLayoutParams);

            ImageButton editWebsiteImageButton = new ImageButton(getApplicationContext());

            LinearLayout.LayoutParams editWebsiteImageButtonLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            editWebsiteImageButtonLayoutParams.gravity=Gravity.RIGHT;
            editWebsiteImageButton.setBackgroundResource(R.drawable.editwebsiteicon);

            editWebsiteImageButtonLayoutParams.setMargins( 0 , 10, 0 , 10 );

            editWebsiteImageButton.setTag(i+100);

            statusLinearLayout.addView(editWebsiteImageButton,editWebsiteImageButtonLayoutParams);

            websiteDetailsLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    int x = (int) v.getTag();
                    TextView foundText = findViewById(x);
                    if(!clickedArrayList.get(x))
                    {
                        foundText.setText(nameArrayList.get(x)+"\n\n"+urlsArraylist.get(x)+"\n\nDaily Limit: "+dailyHitsArrayList.get(x)+"\nTotal Limit: "+totalHitsArrayList.get(x)+"\nHigh Quality: "+highQualityArrayList.get(x));
                        clickedArrayList.set(x,true);
                    }
                    else
                    {
                        foundText.setText(nameArrayList.get(x));
                        clickedArrayList.set(x,false);
                    }
                }
            });

            editWebsiteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    int y = (int) v.getTag();

                    Intent goToEditWebsite = new Intent(getApplicationContext(), EditWebsite.class);
                    goToEditWebsite.putExtra("urlToEdit",urlsArraylist.get(y-100));
                    goToEditWebsite.putExtra("nameToEdit",nameArrayList.get(y-100));
                    goToEditWebsite.putExtra("dailyHitsToEdit",dailyHitsArrayList.get(y-100));
                    goToEditWebsite.putExtra("totalHitsToEdit",totalHitsArrayList.get(y-100));
                    goToEditWebsite.putExtra("idToEdit",idsArrayList.get(y-100));
                    goToEditWebsite.putExtra("timerToEdit",timerArrayList.get(y-100));
                    goToEditWebsite.putExtra("highQualityToEdit",highQualityArrayList.get(y-100));
                    goToEditWebsite.putExtra("categoryToEdit",categoriesArrayList.get(y-100));

                    startActivity(goToEditWebsite);

                }
            });

        }

    }
}