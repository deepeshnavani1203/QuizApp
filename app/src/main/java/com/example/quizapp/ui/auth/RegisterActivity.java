package com.example.quizapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quizapp.R;
import com.example.quizapp.ui.LoginActivity;
import com.example.quizapp.utils.SharedPreferencesManager;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameInput, emailInput, passwordInput;
    private Button registerBtn;
    private TextView loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        registerBtn = findViewById(R.id.registerBtn);
        loginLink = findViewById(R.id.loginLink);

        registerBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferencesManager prefManager = new SharedPreferencesManager(this);
            prefManager.saveUser(name, email, password);

            Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
