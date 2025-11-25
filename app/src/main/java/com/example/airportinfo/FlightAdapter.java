package com.example.airportinfo;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.os.LocaleList;

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.FlightViewHolder> {
    private List<Flight> flights;
    private final Context context;
    private Locale currentLocale;

    public FlightAdapter(List<Flight> flights, Context context) {
        this.flights = flights;
        this.context = context;
        this.currentLocale = getCurrentLocale(context);
    }

    private Locale getCurrentLocale(Context context) {
        Configuration config = context.getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList locales = config.getLocales();
            return locales.isEmpty() ? Locale.getDefault() : locales.get(0);
        } else {
            return config.locale;
        }
    }

    public void updateFlights(List<Flight> newFlights) {
        this.flights = newFlights;
        notifyDataSetChanged();
    }

    public void refreshLocale() {
        this.currentLocale = getCurrentLocale(context);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FlightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flight, parent, false);
        return new FlightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlightViewHolder holder, int position) {
        Flight flight = flights.get(position);
        if (flight == null) return;

        Locale locale = Locale.getDefault();  // Или где ты берёшь текущий язык
        boolean isEnglish = locale.getLanguage().equals("en");

        holder.flightNumber.setText(flight.getFlightNumber());

        if (isEnglish) {
            holder.status.setText(Translator.translateStatus(flight.getStatus()));
            holder.airline.setText(Translator.translateAirline(flight.getAirline()));
            holder.departure.setText(Translator.translateCity(flight.getDeparture()));
            holder.destination.setText(Translator.translateCity(flight.getDestination()));
        } else {
            holder.status.setText(flight.getStatus());
            holder.airline.setText(flight.getAirline());
            holder.departure.setText(flight.getDeparture());
            holder.destination.setText(flight.getDestination());
        }

        holder.departureTime.setText(formatTime(flight.getDepartureTime()));
        holder.arrivalTime.setText(formatTime(flight.getArrivalTime()));

        setStatusColor(holder.status, flight.getStatus());
    }

    private String formatTime(String fullDateTime) {
        if (fullDateTime == null || fullDateTime.isEmpty()) return "—";

        try {
            SimpleDateFormat srcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            Date date = srcFormat.parse(fullDateTime);

            SimpleDateFormat dstFormat = new SimpleDateFormat("HH:mm", currentLocale);
            return dstFormat.format(date);
        } catch (Exception e) {
            return fullDateTime.length() >= 16 ? fullDateTime.substring(11, 16) : fullDateTime;
        }
    }

    private void setStatusColor(TextView statusView, String status) {
        if (status == null || status.isEmpty()) return;

        String statusLower = status.toLowerCase(Locale.US);
        int colorRes;

        switch (statusLower) {
            case "departed":
                colorRes = R.color.colorDeparted;
                break;
            case "scheduled":
                colorRes = R.color.colorScheduled;
                break;
            case "boarding":
                colorRes = R.color.colorBoarding;
                break;
            case "canceled":
                colorRes = R.color.colorCanceled;
                break;
            default:
                colorRes = R.color.colorPrimary;
        }

        statusView.setTextColor(ContextCompat.getColor(context, colorRes));
    }

    @Override
    public int getItemCount() {
        return flights != null ? flights.size() : 0;
    }

    static class FlightViewHolder extends RecyclerView.ViewHolder {
        final TextView flightNumber, status, airline, departure,
                destination, departureTime, arrivalTime;

        public FlightViewHolder(@NonNull View itemView) {
            super(itemView);
            flightNumber = itemView.findViewById(R.id.tvFlightNumber);
            status = itemView.findViewById(R.id.tvStatus);
            airline = itemView.findViewById(R.id.tvAirline);
            departure = itemView.findViewById(R.id.tvDeparture);
            destination = itemView.findViewById(R.id.tvDestination);
            departureTime = itemView.findViewById(R.id.tvDepartureTime);
            arrivalTime = itemView.findViewById(R.id.tvArrivalTime);
        }
    }
}
