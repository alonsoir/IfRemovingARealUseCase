package it.fbonfadelli.hand_baggage;

import it.fbonfadelli.hand_baggage.factory.NotMyCompanyHandBaggageInformationFactory;
import it.fbonfadelli.hand_baggage.policy.HandBaggagePoliciesFactory;
import it.fbonfadelli.model.Flight;
import it.fbonfadelli.model.Order;
import it.fbonfadelli.translation.TranslationRepository;

import java.util.List;

public class HandBaggageInformationFactory {

    private final List<HandBaggageInformationPolicy> handBaggageInformationPolicies;

    public HandBaggageInformationFactory(List<HandBaggageInformationPolicy> handBaggageInformationPolicies) {
        this.handBaggageInformationPolicies = handBaggageInformationPolicies;
    }

    public HandBaggageInformation from(Order order, String renderLanguage, Integer flightId) {
        Flight flight = order.findFlight(flightId);

        NotMyCompanyHandBaggageInformationFactory notMyCompanyHandBaggageInformationFactory =
                new NotMyCompanyHandBaggageInformationFactory();

        return handBaggageInformationPolicies.stream()
                .filter(policy -> policy.canHandle(flight))
                .findFirst()
                .map(policy -> policy.getFrom(renderLanguage))
                .orElse(notMyCompanyHandBaggageInformationFactory.make());
    }

}
