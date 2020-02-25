package com.example.plowed;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ClientUserActivity extends AppCompatActivity {
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_info);
        textView = (TextView) findViewById(R.id.client);
        textView.setText("Welcome " + getIntent().getStringExtra("user"));
    }
}
