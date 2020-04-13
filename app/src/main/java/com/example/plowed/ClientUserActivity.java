package com.example.plowed;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

public class ClientUserActivity extends AppCompatActivity {
    TextView clientName;
    Button toMap;
    EditText address;
    WebView webView;

    private NotificationManagerCompat notificationManager;
    private static final int LOGOUT = 2;
    private static final int DELETE = 3;
    private FirebaseUser mUser;
    private static final String WEATHER = "https://weather.com";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_menu);
        clientName = (TextView) findViewById(R.id.client);
        toMap = (Button) findViewById(R.id.toMap);
        address = (EditText) findViewById(R.id.address);
        webView = (WebView) findViewById(R.id.weather);
        webView.loadUrl(WEATHER);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        userConfig();
    }

    private void userConfig(){
        // may be empty
        if (mUser.getDisplayName() != null){
            clientName.setText(String.format(Locale.ENGLISH, "Welcome %s",
                    mUser.getDisplayName()));
        }else{
            clientName.setText("Welcome!");
        }

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
