package com.example.plowed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RequestService extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener{

    private FirebaseUser mUser;

    Button dateButton;
    Button payButton;
    Button confirmRequest;
    private String userPickedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        setContentView(R.layout.activity_request_service);
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
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("assignments");
        User request = new User(mUser.getDisplayName(), mUser.getEmail(), mUser.getPhoneNumber(), getIntent().getStringExtra("address"));
        db.child(mUser.getUid()).setValue(request);
    }
}
