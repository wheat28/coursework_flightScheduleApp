package com.example.airportinfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailLayout, passwordLayout;
    private TextInputEditText editEmail, editPassword;
    private MaterialButton btnLogin;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Инициализация Firebase
        auth = FirebaseAuth.getInstance();

        // Настройка тулбара
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // скрываем стандартный заголовок
        }

        TextView toolbarTitle = getLayoutInflater().inflate(R.layout.toolbar_title, toolbar, false)
                .findViewById(R.id.toolbar_title);
        toolbar.addView(toolbarTitle);

        // Инициализация элементов
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        MaterialButton btnRegister = findViewById(R.id.btnRegister);

        // Анимация при запуске
        animateViews();

        // Обработчики кликов
        btnLogin.setOnClickListener(v -> loginUser());
        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void animateViews() {
        emailLayout.setAlpha(0f);
        passwordLayout.setAlpha(0f);
        btnLogin.setAlpha(0f);

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

        btnLogin.animate()
                .alpha(1f)
                .setDuration(500)
                .setStartDelay(600)
                .start();
    }

    private void loginUser() {
        // Анимация нажатия кнопки
        btnLogin.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> btnLogin.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start())
                .start();

        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (validateInput(email, password)) {
            btnLogin.setEnabled(false);

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        btnLogin.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Вход выполнен", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, FlightsListActivity.class));
                            finishAffinity();
                        } else {
                            passwordLayout.setError("Неверный email или пароль");
                            passwordLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
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