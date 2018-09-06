package it.fbonfadelli.hand_baggage;

import it.fbonfadelli.hand_baggage.factory.NewMyCompanyHandBaggageInformationFactory;
import it.fbonfadelli.hand_baggage.factory.NotMyCompanyHandBaggageInformationFactory;
import it.fbonfadelli.hand_baggage.factory.OldMyCompanyHandBaggageInformationFactory;
import it.fbonfadelli.model.Flight;
import it.fbonfadelli.model.Order;
import it.fbonfadelli.translation.TranslationRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class HandBaggageInformationFactory {
    private static final String MY_COMPANY_AIRLINE_ID = "MY_COMPANY_AIRLINE_ID";
    private static final LocalDateTime FIRST_OF_NOVEMBER = LocalDateTime.of(2018, 11, 1, 0, 0, 0);
    private static final LocalDate THIRTY_FIRST_OF_OCTOBER = LocalDate.of(2018, 10, 31);

    public HandBaggageInformation from(Order order, TranslationRepository translationRepository, String renderLanguage, Integer flightId) {
        Flight flight = order.findFlight(flightId);
        LocalDateTime flightOutboundDate = flight.getFirstLeg().getFirstHop().getDeparture().getDate();
        LocalDateTime outboundDepartureDate = order.getOutboundDepartureDate();
        LocalDate returnDepartureDate = order.getReturnDepartureDate();

        NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory =
                new NewMyCompanyHandBaggageInformationFactory(translationRepository);
        OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory =
                new OldMyCompanyHandBaggageInformationFactory(translationRepository);
        NotMyCompanyHandBaggageInformationFactory notMyCompanyHandBaggageInformationFactory =
                new NotMyCompanyHandBaggageInformationFactory();

        MyCompanyOneWayAfterTheFirstOfNovember myCompanyOneWayAfterTheFirstOfNovember =
                new MyCompanyOneWayAfterTheFirstOfNovember(newMyCompanyHandBaggageInformationFactory);
        if (myCompanyOneWayAfterTheFirstOfNovember.canHandle(flight, flightOutboundDate)) {
            return myCompanyOneWayAfterTheFirstOfNovember.getFrom(renderLanguage);
        }

        MyCompanyOneWayBeforeTheFirstOfNovember myCompanyOneWayBeforeTheFirstOfNovember =
                new MyCompanyOneWayBeforeTheFirstOfNovember(oldMyCompanyHandBaggageInformationFactory);
        if (myCompanyOneWayBeforeTheFirstOfNovember.canHandle(flight, flightOutboundDate)) {
            return myCompanyOneWayBeforeTheFirstOfNovember.getFrom(renderLanguage);
        }


        if (!flight.isOneWay() && isMyCompany(flight) && (outboundDepartureDate.isAfter(FIRST_OF_NOVEMBER)
                || returnDepartureDate.isAfter(THIRTY_FIRST_OF_OCTOBER))) {
            return newMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }

        if (!flight.isOneWay() && isMyCompany(flight) && (!(outboundDepartureDate.isAfter(FIRST_OF_NOVEMBER)
                || returnDepartureDate.isAfter(THIRTY_FIRST_OF_OCTOBER)))) {
            return oldMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }

        return notMyCompanyHandBaggageInformationFactory.make();
    }

    private boolean isMyCompany(Flight flight) {
        return flight.getAirlineIds().contains(MY_COMPANY_AIRLINE_ID);
    }


    private class MyCompanyOneWayAfterTheFirstOfNovember {

        private final NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory;

        private MyCompanyOneWayAfterTheFirstOfNovember(NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory) {
            this.newMyCompanyHandBaggageInformationFactory = newMyCompanyHandBaggageInformationFactory;
        }

        public boolean canHandle(Flight flight, LocalDateTime flightOutboundDate) {
            return flight.isOneWay()
                    && isMyCompany(flight)
                    && flightOutboundDate.isAfter(FIRST_OF_NOVEMBER);
        }

        public HandBaggageInformation getFrom(String renderLanguage) {
            return this.newMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }
    }

    private class MyCompanyOneWayBeforeTheFirstOfNovember {
        private final OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory;

        private MyCompanyOneWayBeforeTheFirstOfNovember(OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory) {
            this.oldMyCompanyHandBaggageInformationFactory = oldMyCompanyHandBaggageInformationFactory;
        }

        public boolean canHandle(Flight flight, LocalDateTime flightOutboundDate) {
            return flight.isOneWay() && isMyCompany(flight) && !flightOutboundDate.isAfter(FIRST_OF_NOVEMBER);
        }

        private HandBaggageInformation getFrom(String renderLanguage) {
            return this.oldMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }
    }
}
