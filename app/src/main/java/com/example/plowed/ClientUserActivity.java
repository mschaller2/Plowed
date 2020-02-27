package com.example.plowed;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.plowed", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("username", "");
        textView.setText(String.format("Welcome %s", name));

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

    public void goToMap(View view){
        startActivity(new Intent(this, MapsActivity.class));
    }

}
