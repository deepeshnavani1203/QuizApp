package com.example.quizapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.ui.auth.LoginActivity;
import com.example.quizapp.utils.BackendService;
import com.example.quizapp.utils.SharedPreferencesManager;
import org.json.JSONObject;

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
            new Thread(() -> {
                try {
                    JSONObject response = BackendService.signup(name, email, password);
                    if (response != null && response.has("id")) {
                        String userId = response.optString("id");
                        prefManager.saveBackendUser(userId, name, email);
                        prefManager.setLoggedIn(true);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast
                            .makeText(this, "Unable to register. Check your network.", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });

        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
