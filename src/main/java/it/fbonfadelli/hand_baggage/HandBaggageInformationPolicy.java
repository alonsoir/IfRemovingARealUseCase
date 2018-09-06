package it.fbonfadelli.hand_baggage;

import it.fbonfadelli.model.Flight;

public interface HandBaggageInformationPolicy {
    boolean canHandle(Flight flight);

    HandBaggageInformation getFrom(String renderLanguage);
}
