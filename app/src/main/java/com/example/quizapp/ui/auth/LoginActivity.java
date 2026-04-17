package com.example.quizapp.ui.auth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.utils.BackendService;
import com.example.quizapp.utils.NotificationHelper;
import com.example.quizapp.utils.SecurityHelper;
import com.example.quizapp.utils.SharedPreferencesManager;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailInput, passwordInput;
    private Button loginBtn;
    private TextView registerLink, errorTextView;
    private ProgressBar progressBar;
    private SharedPreferencesManager prefManager;

    // Launcher for POST_NOTIFICATIONS permission (Android 13+)
    private final ActivityResultLauncher<String> notifPermLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                // After notification permission, ask for DND
                askDndPermission();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefManager = new SharedPreferencesManager(this);

        if (prefManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        emailInput  = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginBtn    = findViewById(R.id.loginBtn);
        registerLink = findViewById(R.id.registerLink);
        errorTextView = findViewById(R.id.errorTextView);
        progressBar = findViewById(R.id.progressBar);

        loginBtn.setOnClickListener(v -> {
            errorTextView.setVisibility(android.view.View.GONE);
            String email    = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(android.view.View.VISIBLE);
            loginBtn.setEnabled(false);

            new Thread(() -> {
                try {
                    Log.i("LoginActivity", "Attempting login for: " + email);
                    JSONObject response = BackendService.login(email, password);
                    if (response != null && response.has("user")) {
                        JSONObject user = response.getJSONObject("user");
                        String userId    = user.optString("id");
                        String name      = user.optString("name");
                        String userEmail = user.optString("email");
                        Log.i("LoginActivity", "Login success — userId=" + userId + " name=" + name);
                        prefManager.saveBackendUser(userId, name, userEmail);
                        prefManager.setLoggedIn(true);

                        runOnUiThread(() -> {
                            progressBar.setVisibility(android.view.View.GONE);
                            loginBtn.setEnabled(true);
                            askPermissionsOnce();
                        });
                    } else {
                        Log.e("LoginActivity", "Login failed — response=" + (response != null ? response.toString() : "null"));
                        runOnUiThread(() -> {
                            progressBar.setVisibility(android.view.View.GONE);
                            loginBtn.setEnabled(true);
                            errorTextView.setText("Invalid credentials or server unavailable.");
                            errorTextView.setVisibility(android.view.View.VISIBLE);
                        });
                    }
                } catch (Exception e) {
                    Log.e("LoginActivity", "Login exception: " + e.getMessage(), e);
                    runOnUiThread(() -> {
                        progressBar.setVisibility(android.view.View.GONE);
                        loginBtn.setEnabled(true);
                        errorTextView.setText("Unable to connect. Error: " + e.getMessage());
                        errorTextView.setVisibility(android.view.View.VISIBLE);
                    });
                }
            }).start();
        });

        registerLink.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    /**
     * Called once after login. Shows a single permission explanation dialog,
     * then requests notification permission (Android 13+), then DND.
     */
    private void askPermissionsOnce() {
        // Only ask if we haven't asked before
        if (SecurityHelper.hasDndBeenAsked(this)) {
            goToMain();
            return;
        }

        new AlertDialog.Builder(this)
            .setTitle("App Permissions")
            .setMessage(
                "Quizify needs two permissions to work best:\n\n" +
                "🔔  Notifications — for daily study reminders and quiz results.\n\n" +
                "🔕  Do Not Disturb — to silence your phone during quizzes so you're not interrupted.\n\n" +
                "You'll be asked to grant each one now.")
            .setPositiveButton("Continue", (d, w) -> askNotificationPermission())
            .setNegativeButton("Skip", (d, w) -> {
                // Mark as asked so we don't show again
                SecurityHelper.requestDndPermission(this); // marks asked flag
                goToMain();
            })
            .setCancelable(false)
            .show();
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                return;
            }
        }
        // Already granted or not needed — go to DND
        askDndPermission();
    }

    private void askDndPermission() {
        if (!SecurityHelper.hasDndPermission(this)) {
            new AlertDialog.Builder(this)
                .setTitle("Do Not Disturb Access")
                .setMessage("Tap 'Allow' on the next screen and enable Quizify in the list. This lets the app silence your phone during quizzes.")
                .setPositiveButton("Open Settings", (d, w) -> {
                    SecurityHelper.requestDndPermission(this);
                    // Go to main after opening settings (user will return to app)
                    goToMain();
                })
                .setNegativeButton("Skip", (d, w) -> goToMain())
                .setCancelable(false)
                .show();
        } else {
            goToMain();
        }
    }

    private void goToMain() {
        NotificationHelper.createChannel(this);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
