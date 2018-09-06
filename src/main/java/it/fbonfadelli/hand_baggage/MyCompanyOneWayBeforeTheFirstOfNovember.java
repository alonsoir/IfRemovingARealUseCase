package it.fbonfadelli.hand_baggage;

import it.fbonfadelli.hand_baggage.factory.OldMyCompanyHandBaggageInformationFactory;
import it.fbonfadelli.model.Flight;

import java.time.LocalDateTime;

class MyCompanyOneWayBeforeTheFirstOfNovember implements HandBaggageInformationPolicy {
    private static final LocalDateTime FIRST_OF_NOVEMBER = LocalDateTime.of(2018, 11, 1, 0, 0, 0);

    private final OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory;

    MyCompanyOneWayBeforeTheFirstOfNovember(OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory) {
        this.oldMyCompanyHandBaggageInformationFactory = oldMyCompanyHandBaggageInformationFactory;
    }

    @Override
    public boolean canHandle(Flight flight) {
        return flight.isOneWay()
                && flight.isMyCompany()
                && !flight.getOutboundDepartureDate().isAfter(FIRST_OF_NOVEMBER);
    }

    @Override
    public HandBaggageInformation getFrom(String renderLanguage) {
        return this.oldMyCompanyHandBaggageInformationFactory.from(renderLanguage);
    }
}
