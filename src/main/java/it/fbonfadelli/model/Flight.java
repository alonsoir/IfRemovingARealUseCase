package it.fbonfadelli.model;

import java.time.LocalDateTime;
import java.util.HashSet;

public class Flight {
    private static final String MY_COMPANY_AIRLINE_ID = "MY_COMPANY_AIRLINE_ID";

    private int flightId;
    private Legs legs = new Legs();

    public Flight() {
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    int getFlightId() {
        return flightId;
    }

    public void setLegs(Legs legs) {
        this.legs = legs;
    }

    private Legs getLegs() {
        return legs;
    }

    public boolean isOneWay() {
        return legs.size() == 1;
    }

    public Leg getFirstLeg() {
        return getLegs().get(0);
    }

    public HashSet<String> getAirlineIds() {
        HashSet<String> airlineIds = new HashSet<>();

        for (Leg leg : getLegs()) {
            airlineIds.addAll(leg.getAirlineIds());
        }

        return airlineIds;
    }

    Leg getOutboundLeg() {
        return getFirstLeg();
    }

    Leg getReturnLeg() {
        if (isOneWay()) {
            throw new ReturnDataNotAvailableForOneWayException();
        }

        return getLegs().get(1);
    }

    public LocalDateTime getOutboundDepartureDate() {
        return getFirstLeg().getFirstHop().getDeparture().getDate();
    }

    public boolean isMyCompany() {
        return getAirlineIds().contains(MY_COMPANY_AIRLINE_ID);
    }

    public LocalDateTime getReturnDepartureDate() {
        return getReturnLeg().getFirstHop().getDeparture().getDate();
    }

    public boolean hasAllTheDeparturesBefore(LocalDateTime aDate) {
        return hasOutboundDepartureBefore(aDate) && (isOneWay() || isRoundTripWithReturnTakeoffBefore(aDate));
    }

    private boolean hasOutboundDepartureBefore(LocalDateTime aDate) {
        return getOutboundDepartureDate().isBefore(aDate);
    }

    private boolean isRoundTripWithReturnTakeoffBefore(LocalDateTime aDate) {
        return !isOneWay() && getReturnDepartureDate().isBefore(aDate);
    }
}
