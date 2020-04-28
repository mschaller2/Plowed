package com.example.plowed;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseUser;
import java.util.Locale;

public class Listings extends AppCompatActivity {
    private FirebaseUser mUser;

    TextView listings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listings_activity);

        listings = (TextView) findViewById(R.id.listings);
    }



    // Need to dynamically update the listings on the activity
    // fetch from db
    public void fetchListingsFromDB(){

    }

    //Using this function from the user menu to dynamically adjust the listings text
    private void userConfig(){
        // may be empty
        if (mUser.getDisplayName() != null){
            listings.setText(String.format(Locale.ENGLISH, "Listings: %s",
                    mUser.getDisplayName()));
        }else{
            listings.setText(R.string.welcome);
        }

    }
}
