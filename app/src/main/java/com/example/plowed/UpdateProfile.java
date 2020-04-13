package com.example.plowed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateProfile extends AppCompatActivity {

    private static final String VERIFY = "https://mitchellschaller.com/verification";
    private final int PICK_IMAGE = 71;
    private FirebaseStorage storage;
    private StorageReference reference;

    private FirebaseUser mUser;
    private Uri filePath;

    private ImageView profilePic;
    private Button updateProfilePic;
    private Button updateProfile;
    private Button drive;
    private Button reset;
    private EditText name;
    private EditText email;
    private EditText phone;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);
        storage = FirebaseStorage.getInstance();
        reference = storage.getReference();
        profilePic = (ImageView) findViewById(R.id.profilePic);
        updateProfilePic = (Button) findViewById(R.id.updateProfilePic);
        updateProfile = (Button) findViewById(R.id.updateProfile);
        drive = (Button) findViewById(R.id.drive);
        reset = (Button) findViewById(R.id.reset);
        phone = (EditText) findViewById(R.id.phone);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        showProfilePicture();
    }

    private void showProfilePicture(){
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


    public void setProfilePic(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profilePic.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            if(filePath != null)
            {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();
                StorageReference ref = reference.child("images/" + mUser.getUid() + "/profile_pic");
                ref.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(UpdateProfile.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(UpdateProfile.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Uploaded "+(int)progress+"%");
                            }
                        });
            }
        }
    }
    // needs to be updated to handle edges when everything filled out in part
    public void updateProfileInfo(View view){
        // name
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name.getText().toString())
                .build();
        // email
        mUser.updateEmail(email.toString());
        mUser.sendEmailVerification();
        // phone tbd
        //mUser.updatePhoneNumber()
    }
    public void requestToDrive(View view){
        Intent paperwork = new Intent(Intent.ACTION_VIEW, Uri.parse(VERIFY));
        startActivity(paperwork);

    }

    public void sendPasswordReset(View view) {
        // [START send_password_reset]
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = mUser.getEmail();
        if (emailAddress != null){
            auth.sendPasswordResetEmail(emailAddress);
        }else{
            Toast.makeText(this, "Update email first", Toast.LENGTH_LONG).show();
        }

        // [END send_password_reset]
    }

}
