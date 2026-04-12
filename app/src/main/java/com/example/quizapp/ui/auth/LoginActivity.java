package com.example.quizapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.utils.SharedPreferencesManager;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailInput, passwordInput;
    private Button loginBtn;
    private TextView registerLink;
    private SharedPreferencesManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefManager = new SharedPreferencesManager(this);
        
        // Redirect if already logged in
        if (prefManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginBtn = findViewById(R.id.loginBtn);
        registerLink = findViewById(R.id.registerLink);

        loginBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (prefManager.validateLogin(email, password)) {
                prefManager.setLoggedIn(true);
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}
