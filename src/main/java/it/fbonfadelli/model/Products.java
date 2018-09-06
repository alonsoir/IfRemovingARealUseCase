package it.fbonfadelli.model;

import java.time.LocalDate;

public class Products {
    private Flights flights = new Flights();

    Flights getFlights() {
        return flights;
    }

    public void setFlights(Flights flights) {
        this.flights = flights;
    }

    LocalDate getReturnDepartureDate() {
        if (hasFlights()) {
            return flightReturnLegDepartureDate();
        } else {
            return null;
        }
    }

    private LocalDate flightReturnLegDepartureDate() {
        if (this.isOneWay()) {
            return null;
        } else {
            return this.getReturnLeg().getFirstHop().getDeparture().getDate().toLocalDate();
        }
    }

    private boolean isOneWay() {
        return flights.size() == 1 && flights.get(0).isOneWay();
    }

    Leg getOutboundLeg() {
        return getFirstFlight().getOutboundLeg();
    }

    private Flight getFirstFlight() {
        return flights.get(0);
    }

    private boolean isDoubleOneWay() {
        return flights.size() > 1;
    }

    private Leg getReturnLeg() {
        if (isOneWay()) {
            throw new ReturnDataNotAvailableForOneWayException();
        }

        if (isDoubleOneWay()) {
            return getReturnFlight().getFirstLeg();
        }

        return getFirstFlight().getReturnLeg();
    }

    private Flight getReturnFlight() {
        if (flights.size() < 2) {
            throw new ReturnDataNotAvailableForOneWayException();
        }

        return flights.get(1);
    }

    private boolean hasFlights() {
        return !flights.isEmpty();
    }
}
