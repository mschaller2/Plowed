package com.example.plowed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginUIActivity extends AppCompatActivity {

    private static final int SIGN_IN = 123;
    private static final int LOGOUT = 2;
    private static final int DELETE = 3;
    private static final String PRIVACY = "https://mitchellschaller.com/privacy.html";
    private static final String TERMS = "https://mitchellschaller.com/terms.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createSignInIntent();
    }

    public void createSignInIntent(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                        .setTheme(R.style.AppTheme)
                        .setLogo(R.drawable.logo)
                        .setTosAndPrivacyPolicyUrls(TERMS, PRIVACY)
                    .build(),
                SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getStringExtra("action") == null){
            if (requestCode == SIGN_IN){
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if (resultCode == RESULT_OK){
                    // sign in success, do something here, launch to maps
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    startActivityForResult(new Intent(this, ClientUserActivity.class), LOGOUT);
                }else{
                    // sign in fail
                    if (response != null){
                        Log.e("login_error", response.toString());
                        Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
                        // todo check
                        createSignInIntent();
                    }
                    // user pressed back, handle
                }
            }
        }else{
            switch(resultCode){
                case LOGOUT:
                    signOut();
                    break;
                case DELETE:
                    delete();
                    break;
                default:
                    break;
            }
        }
    }
    public void signOut(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        createSignInIntent();
                    }
                });
    }
    public void delete(){
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        createSignInIntent();
                    }
                });
    }

}
