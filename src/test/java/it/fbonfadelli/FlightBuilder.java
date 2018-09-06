package it.fbonfadelli;

import it.fbonfadelli.model.Airlines;
import it.fbonfadelli.model.Flight;
import it.fbonfadelli.model.Leg;
import it.fbonfadelli.model.Legs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FlightBuilder {
    private List<Leg> legs = new ArrayList<>();
    private Airlines airlines = new Airlines() {{
        setMarketingAirline("AZ");
    }};
    private int flightId = 1;

    private FlightBuilder() {
    }

    public static FlightBuilder aFlight() {
        return new FlightBuilder();
    }

    public FlightBuilder addLeg(Leg leg) {
        this.legs.add(leg);
        return this;
    }

    public FlightBuilder withFlightId(int flightId) {
        this.flightId = flightId;
        return this;
    }

    public FlightBuilder withAirline(String marketingAirline) {
        airlines.setMarketingAirline(marketingAirline);
        return this;
    }

    public Flight build() {
        Flight flight = new Flight();
        flight.setFlightId(flightId);

        if (legs.isEmpty()) {
            legs = Collections.singletonList(LegBuilder.aLeg().build());
        } else {
            legs = legs.stream().filter(Objects::nonNull).collect(Collectors.toList());
            legs.stream().forEach(leg -> leg.setFlightId(flightId));
        }

        flight.setLegs(new Legs(legs));
        return flight;
    }
}
