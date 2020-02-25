package com.example.plowed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ClientUserActivity extends AppCompatActivity {
    TextView textView;
    Button toMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_info);
        textView = (TextView) findViewById(R.id.client);
        toMap = (Button) findViewById(R.id.toMap);
        textView.setText("Welcome " + getIntent().getStringExtra("user"));
    }

    public void goToMap(View view){
        startActivity(new Intent(this, ClientMapActivity.class));
    }

}
