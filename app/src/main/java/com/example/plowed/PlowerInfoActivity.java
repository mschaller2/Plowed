package com.example.plowed;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PlowerInfoActivity extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plower_info);
        textView = (TextView) findViewById(R.id.plower);
        textView.setText("Welcome " + getIntent().getStringExtra("user"));
    }
}
