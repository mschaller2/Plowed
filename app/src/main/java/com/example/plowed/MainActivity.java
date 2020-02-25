package com.example.plowed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button login;
    EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = (Button) findViewById(R.id.login);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
    }

    public void login(View view){
        String user = username.getText().toString();
        String pass = password.getText().toString();
        if (user.equals("mitch") && pass.equals("123")) {
            Toast.makeText(this, "Redirecting...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ClientUserActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }else if (user.equals("driver") && pass.equals("123")){
            Toast.makeText(this, "Redirecting...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, PlowerInfoActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }else{
            Toast.makeText(this, "User Not Found", Toast.LENGTH_LONG).show();
        }

    }
}
