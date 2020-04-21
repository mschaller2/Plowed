package com.example.plowed;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.text.DateFormat;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {
    int yearPicked;
    int monthPicked;
    int dayPicked;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);

    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
//        Calendar c = Calendar.getInstance();
//        c.set(Calendar.YEAR, year);
//        c.set(Calendar.MONTH, month);
//        c.set(Calendar.DAY_OF_MONTH, day);
//        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
//        TextView textView = (TextView) findViewById(R.id.textView);
//        textView.setText(currentDateString);

    }
}
