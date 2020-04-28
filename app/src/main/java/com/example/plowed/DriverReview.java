package com.example.plowed;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DriverReview extends AppCompatActivity {
    Button saveRating;
    RatingBar ratingBar;
    EditText writtenRating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_review_activity);

        saveRating = (Button) findViewById(R.id.saveRating);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        writtenRating = (EditText) findViewById(R.id.writtenReview);
        saveRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rating = "Rating is: " + ratingBar.getRating() + " stars";
                Toast.makeText(DriverReview.this, rating, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getRecentDrivers(){

    }
    // based on recent drivers, user can rate them
    // 1 to 5 star ratings
    // User can add written rating
}
