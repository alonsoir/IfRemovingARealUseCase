package it.fbonfadelli.hand_baggage.policy;

import it.fbonfadelli.hand_baggage.HandBaggageInformation;
import it.fbonfadelli.hand_baggage.HandBaggageInformationPolicy;
import it.fbonfadelli.hand_baggage.factory.NewMyCompanyHandBaggageInformationFactory;
import it.fbonfadelli.model.Flight;

import java.time.LocalDateTime;

public class MyCompanyOneWayAfterTheFirstOfNovember implements HandBaggageInformationPolicy {
    private static final LocalDateTime FIRST_OF_NOVEMBER = LocalDateTime.of(2018, 11, 1, 0, 0, 0);

    private final NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory;

    public MyCompanyOneWayAfterTheFirstOfNovember(NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory) {
        this.newMyCompanyHandBaggageInformationFactory = newMyCompanyHandBaggageInformationFactory;
    }

    @Override
    public boolean canHandle(Flight flight) {
        return flight.isMyCompany()
                && !flight.hasAllTheDeparturesBefore(FIRST_OF_NOVEMBER);
    }

    @Override
    public HandBaggageInformation getFrom(String renderLanguage) {
        return this.newMyCompanyHandBaggageInformationFactory.from(renderLanguage);
    }
}
