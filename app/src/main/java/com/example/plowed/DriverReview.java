package com.example.plowed;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DriverReview extends AppCompatActivity {
    Button saveRating;
    RatingBar ratingBar;
    EditText writtenRating;
    ImageView profilePic;
    private FirebaseUser mUser;
    private FirebaseStorage storage;
    private StorageReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_review_activity);
//        reference = storage.getReference();
        profilePic = (ImageView) findViewById(R.id.driverProfilePicture);

        saveRating = (Button) findViewById(R.id.saveRating);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        writtenRating = (EditText) findViewById(R.id.writtenReview);
        saveRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rating = "Rating is: " + ratingBar.getRating() + " stars";
//                String writtenReview = "Rating is: " + writtenRating.getText();
                Toast.makeText(DriverReview.this, rating, Toast.LENGTH_LONG).show();
            }
        });
//        mUser = FirebaseAuth.getInstance().getCurrentUser();
//        showProfilePicture();
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

    public void getRecentDrivers(){

    }
    // based on recent drivers, user can rate them
    // 1 to 5 star ratings
    // User can add written rating
}
