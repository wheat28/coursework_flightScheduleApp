package com.example.airportinfo;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FlightsListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private FlightAdapter flightAdapter;
    private List<Flight> originalFlights;
    private FloatingActionButton fabFilter;
    private DrawerLayout drawerLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(updateBaseContextLocale(newBase));
    }

    private Context updateBaseContextLocale(Context context) {
        String lang = SharedPreferencesHelper.getLanguage(context);
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);
        config.setLayoutDirection(locale);

        return context.createConfigurationContext(config);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        updateConfiguration();

        AppCompatDelegate.setDefaultNightMode(
                SharedPreferencesHelper.isDarkModeEnabled(this)
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );
        setTheme(R.style.Theme_Airportinfo);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flights_list);

        initViews();
        setupToolbar();
        setupNavigationDrawer();
        fetchFlightsFromServer();
    }

    private void updateConfiguration() {
        String lang = SharedPreferencesHelper.getLanguage(this);
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Resources res = getResources();
        Configuration config = res.getConfiguration();
        config.setLocale(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewFlights);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fabFilter = findViewById(R.id.fabFilter);
        fabFilter.setOnClickListener(v -> openFilterActivity());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.current_flights_list);
        }
    }

    private void setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_theme) {
            toggleTheme();
            return true;
        } else if (id == R.id.nav_language) {
            toggleLanguage();
            return true;
        }
        else if (id == R.id.nav_support) {
            startActivity(new Intent(this, SupportActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    private void toggleTheme() {
        boolean darkMode = !SharedPreferencesHelper.isDarkModeEnabled(this);
        SharedPreferencesHelper.setDarkModeEnabled(this, darkMode);
        restartActivity();
    }

    private void toggleLanguage() {
        String currentLang = SharedPreferencesHelper.getLanguage(this);
        String newLang = currentLang.equals("ru") ? "en" : "ru";
        SharedPreferencesHelper.setLanguage(this, newLang);

        if (flightAdapter != null) {
            flightAdapter.refreshLocale();
        }

        restartActivity();
    }


    private void restartActivity() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(this, FlightsListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, 300); // Задержка 300 мс
    }

    private void fetchFlightsFromServer() {
        FlightApi api = ApiClient.getClient().create(FlightApi.class);
        Call<List<Flight>> call = api.getAllFlights();

        call.enqueue(new Callback<List<Flight>>() {
            @Override
            public void onResponse(Call<List<Flight>> call, Response<List<Flight>> response) {
                if (response.isSuccessful()) {
                    originalFlights = response.body();
                    flightAdapter = new FlightAdapter(originalFlights, FlightsListActivity.this);
                    recyclerView.setAdapter(flightAdapter);
                } else {
                    //showToast(R.string.loading_error);
                }
            }

            @Override
            public void onFailure(Call<List<Flight>> call, Throwable t) {
                //showToast(R.string.connection_error);
                Log.e("FlightsListActivity", "Connection error", t);
            }
        });
    }

    private void showToast(int stringResId) {
        Toast.makeText(this, stringResId, Toast.LENGTH_SHORT).show();
    }

    private void openFilterActivity() {
        startActivityForResult(new Intent(this, FilterActivity.class), 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            applyFiltersFromActivity(data);
        }
    }

    private void applyFiltersFromActivity(Intent data) {
        try {
            if (originalFlights == null || data == null) return;

            List<Flight> filtered = new ArrayList<>(originalFlights);
            filterByStatus(data, filtered);
            filterByTime(data, filtered);
            filterByAirline(data, filtered);

            if (flightAdapter != null) {
                flightAdapter.updateFlights(filtered);
            }
        } catch (Exception e) {
            //showToast(R.string.filter_error);
            Log.e("FlightsListActivity", "Filtering error", e);
        }
    }

    private void filterByStatus(Intent data, List<Flight> filtered) {
        if (data.hasExtra("status")) {
            String status = data.getStringExtra("status");
            if (!"all".equals(status)) {
                filtered.removeIf(f ->
                        f.getStatus() == null ||
                                !f.getStatus().trim().equalsIgnoreCase(status.trim())
                );
            }
        }
    }

    private void filterByTime(Intent data, List<Flight> filtered) throws Exception {
        if (data.hasExtra("timeFrom") || data.hasExtra("timeTo")) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String timeFrom = data.getStringExtra("timeFrom");
            String timeTo = data.getStringExtra("timeTo");
            Date from = !TextUtils.isEmpty(timeFrom) ? sdf.parse(timeFrom) : null;
            Date to = !TextUtils.isEmpty(timeTo) ? sdf.parse(timeTo) : null;

            filtered.removeIf(f -> {
                try {
                    String timeStr = f.getDepartureTime().split(" ")[1];
                    Date time = sdf.parse(timeStr);
                    return (from != null && time.before(from)) ||
                            (to != null && time.after(to));
                } catch (Exception e) {
                    return false;
                }
            });
        }
    }

    private void filterByAirline(Intent data, List<Flight> filtered) {
        if (data.hasExtra("airline")) {
            String airline = data.getStringExtra("airline");
            filtered.removeIf(f -> !airline.equals(f.getAirline()));
        }
    }
    @Override
    protected void onDestroy() {
        if (flightAdapter != null) {
            flightAdapter = null;
        }
        super.onDestroy();
    }
}