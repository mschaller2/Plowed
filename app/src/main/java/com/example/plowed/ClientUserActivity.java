package com.example.plowed;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.androdocs.httprequest.HttpRequest;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientUserActivity extends AppCompatActivity {
    private static final String ZIP_REGEX ="^\\d{5}$";
    TextView clientName;
    TextView temperature;
    Button toMap;
    EditText address;
    EditText zipCodeIn;

    private NotificationManagerCompat notificationManager;
    private static final int LOGOUT = 2;
    private static final int DELETE = 3;
    private FirebaseUser mUser;
    private String zipCode;
    private LocationManager locationManager;
    private Location location;
    private boolean zipEdit;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_menu);
        clientName = (TextView) findViewById(R.id.client);
        toMap = (Button) findViewById(R.id.toMap);
        address = (EditText) findViewById(R.id.address);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        temperature = (TextView) findViewById(R.id.temperature);
        temperature.setText(R.string.temp);
        zipCodeIn = (EditText) findViewById(R.id.zipCodeIn);
        pref = getSharedPreferences("com.example.plowed", Context.MODE_PRIVATE);

        zipCodeIn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                zipEdit = true;
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT < 23) {
            startListening();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                startListening();
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        userConfig();

    }
    private void userConfig(){
        // may be empty
        if (mUser.getDisplayName() != null){
            clientName.setText(String.format(Locale.ENGLISH, "Welcome %s",
                    mUser.getDisplayName()));
        }else{
            clientName.setText(R.string.welcome);
        }

    }
    // On-clicks
    public void goToMap(View view){
        Intent toMap = new Intent(this, MapsActivity.class);
        toMap.putExtra("location", location);
        toMap.putExtra("zip", zipCode);
        toMap.putExtra("manual", zipEdit);
        startActivity(toMap);
    }
    public void goToConfirmationListing(View view){ startActivity(new Intent(this, ConfirmListing.class)); }

    // Menu callbacks
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

    // Location setting section
    public void startListening(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null){
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            updateLocationInfo(location);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startListening();
        }
    }
    public void updateLocationInfo(Location location) {
        if (location != null) {
            Geocoder geo = new Geocoder(this, Locale.getDefault());
            try{
                List<Address> address = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                zipCode = address.get(0).getPostalCode();
                pref.edit().putString("zip", zipCode).apply();
            }catch(Exception e){
                Log.e("update loc info error", e.toString());
            }
        }
    }
    // END LOCATION

    // Weather service section
    public void updateWeather(View view) {
        if(location == null || zipEdit){
            if (zipEdit && zipCodeIn.getText().toString().matches(ZIP_REGEX)){
                zipCode = zipCodeIn.getText().toString();
                pref.edit().putString("zip", zipCode).apply();
                new weatherTask().execute();
            }else{
                Toast.makeText(this, "Invalid zip.", Toast.LENGTH_LONG).show();
            }
        }else{
            new weatherTask().execute();
        }
    }
    // Weather class
    class weatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected String doInBackground(String... args) {
            return HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?zip=" + zipCode + "&appid=" + getText(R.string.open_weather_key) +"&units=imperial");
        }
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                String temp = main.getString("temp") ;
                temperature.setText(String.format("%s Degrees F", temp));
            } catch (JSONException e) {
                Log.e("weather error", e.toString());
            }

        }
    }
    // END WEATHER

    // NOTIFS TODO
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



}
