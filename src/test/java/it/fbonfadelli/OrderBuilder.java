package it.fbonfadelli;

import it.fbonfadelli.model.Flight;
import it.fbonfadelli.model.Flights;
import it.fbonfadelli.model.Order;

import static it.fbonfadelli.FlightBuilder.aFlight;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class OrderBuilder {
    private Flights flights = new Flights(singletonList(aFlight().build()));

    public static OrderBuilder anOrder() {
        return new OrderBuilder();
    }

    public Order build() {
        Order order = new Order();
        order.getProducts().setFlights(flights);
        return order;
    }

    public OrderBuilder withFlights(Flight... flights) {
        this.flights = new Flights(asList(flights));
        return this;
    }
}
