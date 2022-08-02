package com.cs5520.assignments.numad22su_team24_puddle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.cs5520.assignments.numad22su_team24_puddle.Utils.FirebaseDB;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.Util;
import com.google.android.material.textfield.TextInputEditText;

public class SignupActivity extends AppCompatActivity {

    private Button signupBtn;
    private TextInputEditText emailEditText, usernameEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupBtn = findViewById(R.id.register_btn);
        emailEditText = findViewById(R.id.signup_email_et);
        usernameEditText = findViewById(R.id.signup_username_et);
        passwordEditText = findViewById(R.id.signup_password_et);

        signupBtn.setOnClickListener(v -> {
            // Implement Firebase signup logic
            if (passwordEditText.getText().toString().isEmpty()) {
                Toast.makeText(this, "Enter a valid password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Util.isNetworkConnected(this)) {
                Toast.makeText(this, "Please connect to internet!", Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseDB.registerUser(usernameEditText.getText().toString() + "@puddle.com"
                    , passwordEditText.getText().toString()
                    , usernameEditText.getText().toString()
                    , SignupActivity.this);
        });
    }
}