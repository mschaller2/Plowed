package com.example.plowed;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.example.plowed.App.CHANNEL_1_ID;

public class ClientUserActivity extends AppCompatActivity {
    TextView textView;
    Button toMap;
    private NotificationManagerCompat notificationManager;
    //private EditText editTextTitle;
    //private EditText editTextMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_info);
        textView = (TextView) findViewById(R.id.client);
        toMap = (Button) findViewById(R.id.toMap);
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.plowed", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("username", "");
        textView.setText(String.format("Welcome %s", name));

        //notificationManager = NotificationManagerCompat.from(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_resources, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch(item.getItemId()){
            case R.id.logout:
                Intent intent = new Intent(this, LoginActivity.class);
                SharedPreferences sharedPreferences = getSharedPreferences("com.example.plowed", Context.MODE_PRIVATE);
                sharedPreferences.edit().remove("username").apply();
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
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

    public void goToMap(View view){
        startActivity(new Intent(this, MapsActivity.class));
    }

    public void goToConfirmationListing(View view){
        startActivity(new Intent(this, ConfirmListing.class));
    }

}
