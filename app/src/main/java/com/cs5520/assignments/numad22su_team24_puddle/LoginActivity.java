package com.cs5520.assignments.numad22su_team24_puddle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cs5520.assignments.numad22su_team24_puddle.Utils.FirebaseDB;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.Util;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton, signupButton;
    private TextInputEditText usernameEditText, passwordEditText;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        firebaseUser = auth.getCurrentUser();

        // checking for users existence: Saving the current user
        if (firebaseUser != null) {
            startActivity(new Intent(LoginActivity.this, PuddleListActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        loginButton = findViewById(R.id.button_login);
        signupButton = findViewById(R.id.button_signup);
        usernameEditText = findViewById(R.id.username_et);
        passwordEditText = findViewById(R.id.login_password_et);

        boolean showToast = getIntent().getBooleanExtra("showToast", false);
        if(showToast) {
            Toast.makeText(this, "Please login/signup to join the Puddle!", Toast.LENGTH_SHORT).show();
        }

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if(!username.equals("") && !password.equals("")){
                if (!Util.isNetworkConnected(this)) {
                    Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseDB.getInstanceFirebaseAuth().signInWithEmailAndPassword(
                        usernameEditText.getText().toString().trim() + "@puddle.com",
                        passwordEditText.getText().toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Successfully Logged in
                        Intent intent = new Intent(LoginActivity.this, PuddleListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("loginUserName", usernameEditText.getText().toString().trim());
                        finish();
                        startActivity(intent);
                    } else {
                        // Error
                        Toast.makeText(LoginActivity.this, "Error logging in! Please check the credentials.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Please enter username and password to login!", Toast.LENGTH_SHORT).show();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}