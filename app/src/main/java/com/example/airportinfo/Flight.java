package com.example.airportinfo;

import com.google.gson.annotations.SerializedName;

public class Flight {
    @SerializedName("flight_number") private String flightNumber;
    @SerializedName("status") private String status;
    @SerializedName("airline") private String airline;
    @SerializedName("departure") private String departure;
    @SerializedName("destination") private String destination;
    @SerializedName("departure_time") private String departureTime;
    @SerializedName("arrival_time") private String arrivalTime;

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAirline() { return airline; }
    public void setAirline(String airline) { this.airline = airline; }
    public String getDeparture() { return departure; }
    public void setDeparture(String departure) { this.departure = departure; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
}