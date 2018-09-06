package it.fbonfadelli.model;

import java.util.Set;
import java.util.stream.Collectors;

public class Leg {
    private int flightId;
    private int legId;
    private int stops;
    private Hops hops = new Hops();

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public int getLegId() {
        return legId;
    }

    public void setLegId(int legId) {
        this.legId = legId;
    }

    public int getStops() {
        return stops;
    }

    public void setStops(int stops) {
        this.stops = stops;
    }

    public Hops getHops() {
        return hops;
    }

    public Hop getFirstHop() {
        return getHops().get(0);
    }

    public PlaceTime getDeparture() {
        return getFirstHop().getDeparture();
    }

    Set<String> getAirlineIds() {
        Set<String> airlineIds = getHops().stream()
                .map(hop -> hop.getHopFlight().getAirlineId())
                .collect(Collectors.toSet());

        return airlineIds;
    }

    public void setHops(Hops hops) {
        this.hops = hops;
    }
}

