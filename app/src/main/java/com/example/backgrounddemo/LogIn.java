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
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.JsonObject;
import org.json.JSONObject;
import java.io.IOException;

public class LogIn extends AppCompatActivity {

    OkHttpClient client;

    SharedPreferences sharedPreferences;

    EditText username;
    EditText password;

    String token;

    public void Login(View view)
    {
        JsonObject postData = new JsonObject();

        postData.addProperty("username", username.getText().toString());
        postData.addProperty("password", password.getText().toString());

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody postBody = RequestBody.create(postData.toString(),JSON);

        Request post = new Request.Builder()
                .url("https://doorstepdelhi-test.herokuapp.com/rest-auth/login/")
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

                        token = myJsonObject.getString("key");
                        Log.i("token is: ",token);

                        sharedPreferences.edit().putString("token",token).apply();

                        Intent goToMainActivity = new Intent(getApplicationContext(),MainActivity.class);
                        goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(goToMainActivity);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(LogIn.this,"Invalid credentials",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        sharedPreferences = this.getSharedPreferences("com.example.backgrounddemo", Context.MODE_PRIVATE);

        client = new OkHttpClient();

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
    }
}