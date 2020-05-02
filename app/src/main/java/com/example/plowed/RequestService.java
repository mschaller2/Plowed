package com.example.plowed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.plowed.App.CHANNEL_1_ID;

public class RequestService extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener{

    private FirebaseUser mUser;
    private NotificationManagerCompat notificationManager;
    Button dateButton;
    Button payButton;
    Button confirmRequest;
    private String userPickedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        setContentView(R.layout.activity_request_service);
        notificationManager = NotificationManagerCompat.from(this);
        dateButton = (Button) findViewById(R.id.dateButton);
        dateButton.setOnClickListener(this);
        payButton = (Button) findViewById(R.id.payButton);
        payButton.setOnClickListener(this);
        confirmRequest = (Button) findViewById(R.id.confirmRequest);
        confirmRequest.setOnClickListener(this);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        userPickedDate = currentDateString;

        TextView textView = (TextView) findViewById(R.id.dateText);
        Button dateButton = (Button) findViewById(R.id.dateButton);
        textView.setText(currentDateString);

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.dateButton:
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
                break;
            case R.id.payButton:
                startActivity(new Intent(this, HandlePayment.class));
                break;
            case R.id.confirmRequest:
                sendRequestToDB();
                break;
        }
    }
    // This function is called in the on Click function. When the confirm request button is pressed
    // all of the necessary information should be sent to the DB
    private void sendRequestToDB(){
        DatabaseReference db = FirebaseDatabase.getInstance().
                getReference(String.format("assignments/%s", mUser.getUid()));
        User request = new User(mUser.getDisplayName(), mUser.getEmail(), mUser.getPhoneNumber(),
                getIntent().getStringExtra("address"), mUser.getUid());
        db.setValue(request);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (!dataSnapshot.exists()){
                   sendNotification();
               }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    public void sendNotification(){
        Intent activityIntent = new Intent(this, DriverReview.class);
        activityIntent.putExtra("name", "Mitchell Schaller");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Plowing Request Accepted")
                // hard coded name for now
                .setContentText("Mitchell Schaller will contact you shortly.\nTap to review!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build();
        notificationManager.notify(1, notification);
    }
}
