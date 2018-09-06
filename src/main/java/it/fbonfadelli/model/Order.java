package it.fbonfadelli.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Order {
    private Products products = new Products();

    public Products getProducts() {
        return products;
    }

    public LocalDateTime getOutboundDepartureDate() {
        return getOutboundLeg().getDeparture().getDate();
    }

    public LocalDate getReturnDepartureDate() {
        return products.getReturnDepartureDate();
    }

    public Leg getOutboundLeg() {
        return products.getOutboundLeg();
    }

    public Flight findFlight(int flightId) {
        return products.getFlights().stream().filter(flight -> flight.getFlightId() == flightId).findFirst()
                .orElse(null);
    }
}
