package org.example;

public class TrainInfo {
    String origin;
    String destination;
    String expectedArrival;
    String expectedDeparture;
    String destinationTime;
    String trainCode;
    String duein;
    String status;
    String direction;


    @Override
    public String toString() {
        return String.format("Train %s: %s → %s, Arrival: %s, Departure: %s, Status: %s",
                trainCode, origin, destination, expectedArrival, expectedDeparture, status);
    }
}
