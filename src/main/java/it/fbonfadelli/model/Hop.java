package it.fbonfadelli.model;

public class Hop {
    private int flightId;
    private int legId;
    private int hopId;
    private PlaceTime departure;
    private PlaceTime arrival;
    private HopFlight hopFlight = new HopFlight();

    public PlaceTime getDeparture() {
        return departure;
    }

    public HopFlight getHopFlight() {
        return hopFlight;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public void setLegId(int legId) {
        this.legId = legId;
    }

    public void setHopId(int hopId) {
        this.hopId = hopId;
    }

    public void setDeparture(PlaceTime departure) {
        this.departure = departure;
    }

    public void setArrival(PlaceTime arrival) {
        this.arrival = arrival;
    }

    public void setHopFlight(HopFlight hopFlight) {
        this.hopFlight = hopFlight;
    }
}
