package com.example.plowed;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import com.androdocs.httprequest.HttpRequest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.plowed.App.CHANNEL_1_ID;

public class ClientUserActivity extends AppCompatActivity {
    TextView clientName;
    TextView temperature;
    Button toMap;
    EditText address;

    private NotificationManagerCompat notificationManager;
    private static final int LOGOUT = 2;
    private static final int DELETE = 3;
    private FirebaseUser mUser;
    String City = "madison";
    String API = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_info);
        clientName = (TextView) findViewById(R.id.client);
        toMap = (Button) findViewById(R.id.toMap);
        address = (EditText) findViewById(R.id.address);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        temperature = (TextView) findViewById(R.id.temperature);
        temperature.setText("Click Button To Update Temperature");
        userConfig();
    }

    public void updateWeather(View view) {
        new weatherTask().execute();
    }

    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=" + City + "&appid=" + API+"&units=imperial");
            return response;
        }

        @Override
        protected void onPostExecute(String result) {


            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                String temp = main.getString("temp") ;
                temperature.setText(temp + "Degrees F");

            } catch (JSONException e) {

            }

        }
    }

    private void userConfig(){
        // may be empty
        clientName.setText(String.format(Locale.ENGLISH, "Welcome %s",
                mUser.getDisplayName()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_resources, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        Intent intent = new Intent(this, LoginUIActivity.class);
        intent.putExtra("action", "true");
        switch(item.getItemId()){
            case R.id.logout:
                setResult(LOGOUT, intent);
                finish();
                break;
            case R.id.delete:
                setResult(DELETE, intent);
                finish();
                break;
            case R.id.settings:
                Intent update = new Intent(this, UpdateProfile.class);
                startActivity(update);
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    /*
    public void sendNotification(View v){
        //String title = editTextTitle.getText().toString();
        //String message = editTextMessage.getText().toString();

        Intent activityIntent = new Intent(this, ClientUserActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);

        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        //broadcastIntent.putExtra("toastMessage", message);
        PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_chat_black_24dp)
                //.setContentTitle(title)
                //.setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.BLUE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .addAction(R.mipmap.ic_launcher, "Toast", actionIntent)
                .build();

        notificationManager.notify(1, notification);
    }
     */

    public void goToMap(View view){
        startActivity(new Intent(this, MapsActivity.class));
    }

    public void goToConfirmationListing(View view){
        startActivity(new Intent(this, ConfirmListing.class));
    }




}
