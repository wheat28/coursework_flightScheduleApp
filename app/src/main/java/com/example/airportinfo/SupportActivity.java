package com.example.airportinfo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SupportActivity extends AppCompatActivity {

    private EditText editTextSubject;
    private EditText editTextMessage;
    private Button buttonSend;

    protected void onCreate(Bundle savedInstanceState) {
        if (SharedPreferencesHelper.isDarkModeEnabled(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        editTextSubject = findViewById(R.id.editTextSubject);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subject = editTextSubject.getText().toString().trim();
                String message = editTextMessage.getText().toString().trim();

                if (subject.isEmpty() || message.isEmpty()) {
                    Toast.makeText(SupportActivity.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SupportActivity.this, "Ваше сообщение отправлено", Toast.LENGTH_LONG).show();
                    editTextSubject.setText("");
                    editTextMessage.setText("");
                }
            }
        });
    }
}
