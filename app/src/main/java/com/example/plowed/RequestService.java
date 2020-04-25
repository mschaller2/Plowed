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

import java.text.DateFormat;
import java.util.Calendar;

public class RequestService extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener{

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
        textView.setText(currentDateString);
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

    public void sendRequestToDB(){

    }
}
