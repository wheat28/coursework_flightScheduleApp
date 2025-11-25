package com.example.airportinfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends AppCompatActivity {
    private static final String TAG = "FilterActivity";
    private RadioGroup statusRadioGroup;
    private EditText timeFromEditText, timeToEditText;
    private Spinner airlineSpinner;
    private List<String> airlines;
    private final String ALL_AIRLINES = "Все авиакомпании";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        initViews();
        setupAirlinesSpinner();
        setupTimeInputs();
        setupButtons();
    }

    private void initViews() {
        statusRadioGroup = findViewById(R.id.statusRadioGroup);
        timeFromEditText = findViewById(R.id.timeFromEditText);
        timeToEditText = findViewById(R.id.timeToEditText);
        airlineSpinner = findViewById(R.id.airlineSpinner);
    }

    private void setupAirlinesSpinner() {
        airlines = new ArrayList<>();
        airlines.add(ALL_AIRLINES);
        airlines.add("Аэрофлот");
        airlines.add("Победа");
        airlines.add("S7 Airlines");
        airlines.add("Уральские авиалинии");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, airlines);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        airlineSpinner.setAdapter(adapter);
    }

    private void setupTimeInputs() {
        timeFromEditText.addTextChangedListener(new TimeTextWatcher(timeFromEditText));
        timeToEditText.addTextChangedListener(new TimeTextWatcher(timeToEditText));
    }

    private void setupButtons() {
        Button applyButton = findViewById(R.id.applyFilterButton);
        Button resetButton = findViewById(R.id.resetFilterButton);

        applyButton.setOnClickListener(v -> applyFilters());
        resetButton.setOnClickListener(v -> resetFilters());
    }

    private void applyFilters() {
        try {
            if (!validateTimeInputs()) {
                return;
            }

            Intent resultIntent = new Intent();

            applyStatusFilter(resultIntent);
            applyTimeFilter(resultIntent);
            applyAirlineFilter(resultIntent);

            setResult(RESULT_OK, resultIntent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Filter application error", e);
            Toast.makeText(this, "Ошибка при фильтрации", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateTimeInputs() {
        String from = timeFromEditText.getText().toString();
        String to = timeToEditText.getText().toString();

        if (!TextUtils.isEmpty(from) && !isValidTimeFormat(from)) {
            timeFromEditText.setError("Формат: ЧЧ:ММ");
            return false;
        }

        if (!TextUtils.isEmpty(to) && !isValidTimeFormat(to)) {
            timeToEditText.setError("Формат: ЧЧ:ММ");
            return false;
        }

        return true;
    }

    private boolean isValidTimeFormat(String time) {
        return time.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
    }

    private void applyStatusFilter(Intent intent) {
        int selectedId = statusRadioGroup.getCheckedRadioButtonId();

        if (selectedId == R.id.radioAll) {
            intent.putExtra("status", "all");
        } else if (selectedId == R.id.radioScheduled) {
            intent.putExtra("status", "По расписанию");
        } else if (selectedId == R.id.radioDelayed) {
            intent.putExtra("status", "Задержан");
        } else if (selectedId == R.id.radioCanceled) {
            intent.putExtra("status", "Отменен");
        } else if (selectedId == R.id.radioBoarding) {
            intent.putExtra("status", "Посадка");
        }
    }

    private void applyTimeFilter(Intent intent) {
        String from = timeFromEditText.getText().toString();
        String to = timeToEditText.getText().toString();

        if (!TextUtils.isEmpty(from)) {
            intent.putExtra("timeFrom", from);
        }
        if (!TextUtils.isEmpty(to)) {
            intent.putExtra("timeTo", to);
        }
    }

    private void applyAirlineFilter(Intent intent) {
        String selected = airlineSpinner.getSelectedItem().toString();
        if (!selected.equals(ALL_AIRLINES)) {
            intent.putExtra("airline", selected);
        }
    }

    private void resetFilters() {
        statusRadioGroup.check(R.id.radioAll);
        timeFromEditText.setText("");
        timeToEditText.setText("");
        airlineSpinner.setSelection(0);
    }

    private class TimeTextWatcher implements TextWatcher {
        private final EditText editText;
        private boolean isFormatting;

        TimeTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (isFormatting) return;

            String current = s.toString();
            if (current.length() == 2 && before == 0 && !current.contains(":")) {
                isFormatting = true;
                editText.setText(current + ":");
                editText.setSelection(3);
                isFormatting = false;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!isFormatting && !s.toString().isEmpty()) {
                editText.setError(isValidTimeFormat(s.toString()) ? null : "Формат: ЧЧ:ММ");
            }
        }
    }
}