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

import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RequestService extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener{

    private FirebaseUser mUser;
    private Long epochTimestamp;
    private Date currentTime;
    private String date;
    private String userName;
    private String userPhone;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_service);

        Button dateButton = (Button) findViewById(R.id.dateButton);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

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
                setRequestToDB();
                sendRequestToDB(epochTimestamp, userName, userPhone, userEmail);
                break;
        }
    }

    public void sendRequestToDB(Long timestamp, String userName, String userPhone, String userEmail){

//        Database db = new Database; // creating a new instance of a database object
//
//        //sending a key value pair
//        db.send(timestamp, userName);
//        db.send(timestamp, userPhone);
//        db.send(timestamp, userEmail);

    }

    // This function is called in the on Click function. When the confirm request button is pressed
    // all of the necessary information should be sent to the DB
    public void setRequestToDB(){
        Long tsLong = System.currentTimeMillis()/1000;
        epochTimestamp = tsLong; // Unique identifier

        //get timestamp
        currentTime = Calendar.getInstance().getTime();
        date = new SimpleDateFormat("dd-MMM-YYYY", Locale.getDefault()).format(new Date());

        //get users name
        userName = mUser.getDisplayName();
        //users contact info
        userPhone = mUser.getPhoneNumber();
        //get address
        userEmail = mUser.getEmail();
        //get payment info?


    }
}
