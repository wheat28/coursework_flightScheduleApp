package com.example.airportinfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface FlightApi {
    @GET("/api/flights")
    Call<List<Flight>> getAllFlights();
}
