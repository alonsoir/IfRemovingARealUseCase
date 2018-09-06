package it.fbonfadelli.hand_baggage;

import it.fbonfadelli.hand_baggage.factory.NewMyCompanyHandBaggageInformationFactory;
import it.fbonfadelli.model.Flight;

import java.time.LocalDateTime;

class MyCompanyOneWayAfterTheFirstOfNovember implements HandBaggageInformationPolicy {
    private static final LocalDateTime FIRST_OF_NOVEMBER = LocalDateTime.of(2018, 11, 1, 0, 0, 0);

    private final NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory;

    MyCompanyOneWayAfterTheFirstOfNovember(NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory) {
        this.newMyCompanyHandBaggageInformationFactory = newMyCompanyHandBaggageInformationFactory;
    }

    @Override
    public boolean canHandle(Flight flight) {
        return flight.isOneWay()
                && flight.isMyCompany()
                && flight.getOutboundDepartureDate().isAfter(FIRST_OF_NOVEMBER);
    }

    @Override
    public HandBaggageInformation getFrom(String renderLanguage) {
        return this.newMyCompanyHandBaggageInformationFactory.from(renderLanguage);
    }
}
