package com.example.plowed;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DriverReview extends AppCompatActivity {
    Button saveRating;
    RatingBar ratingBar;
    EditText driverName;
    ImageView profilePic;
    Button goHome;
    private FirebaseUser mUser;
    private FirebaseStorage storage;
    private StorageReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_review_activity);
        profilePic = (ImageView) findViewById(R.id.driverProfilePicture);
        saveRating = (Button) findViewById(R.id.saveRating);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        driverName = (EditText) findViewById(R.id.writtenReview);
        driverName.setText(getIntent().getStringExtra("name"));
        goHome = (Button) findViewById(R.id.homeButton);
        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ClientUserActivity.class));
            }
        });
        saveRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Not actually validating that such a driver exists
                final DatabaseReference db = FirebaseDatabase.getInstance().
                        getReference(String.format("drivers/%s/ratings", driverName.getText().toString()));
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        db.child(String.format("rating%d", ((int)dataSnapshot.getChildrenCount() + 1))).setValue(ratingBar.getRating());
                        String rating = String.format("%s-Star Rating Saved. Thanks!",
                                ratingBar.getRating());
                        Toast.makeText(DriverReview.this, rating, Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
    private void showProfilePicture(){ // From Update Profile
        StorageReference ref = reference.child("images/" + mUser.getUid() + "/profile_pic");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                Log.i("Image URL", imageURL);
                Glide.with(getApplicationContext()).load(imageURL).into(profilePic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("Image", "Image download failure");
            }
        });
    }

}
