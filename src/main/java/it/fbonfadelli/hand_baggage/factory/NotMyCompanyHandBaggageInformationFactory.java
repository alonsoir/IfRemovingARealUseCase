package it.fbonfadelli.hand_baggage.factory;

import it.fbonfadelli.hand_baggage.HandBaggageInformation;

public class NotMyCompanyHandBaggageInformationFactory {
    public HandBaggageInformation make() {
        return new HandBaggageInformation(
                null,
                true,
                null
        );
    }
}
