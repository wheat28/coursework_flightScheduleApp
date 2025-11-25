package com.example.airportinfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout emailLayout, passwordLayout;
    private TextInputEditText editEmail, editPassword;
    private MaterialButton btnRegister;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Регистрация пользователя");
        }

        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnRegister = findViewById(R.id.btnRegister);

        animateViews();

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void animateViews() {
        emailLayout.setAlpha(0f);
        passwordLayout.setAlpha(0f);
        btnRegister.setAlpha(0f);

        emailLayout.animate()
                .alpha(1f)
                .setDuration(500)
                .setStartDelay(200)
                .start();

        passwordLayout.animate()
                .alpha(1f)
                .setDuration(500)
                .setStartDelay(400)
                .start();

        btnRegister.animate()
                .alpha(1f)
                .setDuration(500)
                .setStartDelay(600)
                .start();
    }

    private void registerUser() {
        btnRegister.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> btnRegister.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start())
                .start();

        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (validateInput(email, password)) {
            btnRegister.setEnabled(false);

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        btnRegister.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, FlightsListActivity.class));
                            finishAffinity();
                        } else {
                            emailLayout.setError("Ошибка регистрации");
                            emailLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
                            Toast.makeText(this, "Ошибка: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private boolean validateInput(String email, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Введите корректный email");
            emailLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
            valid = false;
        } else {
            emailLayout.setError(null);
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordLayout.setError("Пароль должен содержать минимум 6 символов");
            passwordLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
            valid = false;
        } else {
            passwordLayout.setError(null);
        }

        return valid;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}