package it.fbonfadelli.hand_baggage;

import it.fbonfadelli.hand_baggage.factory.NotMyCompanyHandBaggageInformationFactory;
import it.fbonfadelli.model.Flight;
import it.fbonfadelli.model.Order;

import java.util.List;

public class HandBaggageInformationFactory {

    private final List<HandBaggageInformationPolicy> handBaggageInformationPolicies;
    private final NotMyCompanyHandBaggageInformationFactory fallbackHandBaggageFactory;

    public HandBaggageInformationFactory(List<HandBaggageInformationPolicy> handBaggageInformationPolicies,
                                         NotMyCompanyHandBaggageInformationFactory fallbackHandBaggageFactory) {
        this.handBaggageInformationPolicies = handBaggageInformationPolicies;
        this.fallbackHandBaggageFactory = fallbackHandBaggageFactory;
    }

    public HandBaggageInformation from(Order order, String renderLanguage, Integer flightId) {
        Flight flight = order.findFlight(flightId);
        return handBaggageInformationFor(renderLanguage, flight);
    }

    private HandBaggageInformation handBaggageInformationFor(String renderLanguage, Flight flight) {
        return handBaggageInformationPolicies
                .stream()
                .filter(policy -> policy.canHandle(flight))
                .findFirst()
                .map(policy -> policy.getFrom(renderLanguage))
                .orElse(fallbackHandBaggageFactory.make());
    }

}
