package it.fbonfadelli;

import it.fbonfadelli.model.Hop;
import it.fbonfadelli.model.Hops;
import it.fbonfadelli.model.Leg;

import java.util.Collections;
import java.util.List;

import static it.fbonfadelli.HopBuilder.aHop;
import static java.util.Arrays.asList;

public class LegBuilder {
    private List<Hop> hops = Collections.singletonList(aHop().build());

    private LegBuilder() {
    }

    public static LegBuilder aLeg() {
        return new LegBuilder();
    }

    public Leg build() {
        Leg leg = new Leg();
        leg.setFlightId(1);
        int legId = 1;
        leg.setLegId(legId);

        for (Hop hop : hops) {
            hop.setLegId(legId);
            int flightId = 1;
            hop.setFlightId(flightId);
        }

        leg.setHops(new Hops(hops));
        return leg;
    }

    public LegBuilder withHops(Hop... hops) {
        this.hops = asList(hops);
        return this;
    }
}
