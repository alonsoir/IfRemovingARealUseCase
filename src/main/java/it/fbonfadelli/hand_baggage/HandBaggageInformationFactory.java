package it.fbonfadelli.hand_baggage;

import it.fbonfadelli.hand_baggage.factory.NewMyCompanyHandBaggageInformationFactory;
import it.fbonfadelli.hand_baggage.factory.NotMyCompanyHandBaggageInformationFactory;
import it.fbonfadelli.hand_baggage.factory.OldMyCompanyHandBaggageInformationFactory;
import it.fbonfadelli.model.Flight;
import it.fbonfadelli.model.Order;
import it.fbonfadelli.translation.TranslationRepository;

import java.time.LocalDateTime;

public class HandBaggageInformationFactory {
    private static final LocalDateTime FIRST_OF_NOVEMBER = LocalDateTime.of(2018, 11, 1, 0, 0, 0);

    public HandBaggageInformation from(Order order, TranslationRepository translationRepository, String renderLanguage, Integer flightId) {
        Flight flight = order.findFlight(flightId);

        NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory =
                new NewMyCompanyHandBaggageInformationFactory(translationRepository);
        OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory =
                new OldMyCompanyHandBaggageInformationFactory(translationRepository);
        NotMyCompanyHandBaggageInformationFactory notMyCompanyHandBaggageInformationFactory =
                new NotMyCompanyHandBaggageInformationFactory();

        MyCompanyOneWayAfterTheFirstOfNovember myCompanyOneWayAfterTheFirstOfNovember =
                new MyCompanyOneWayAfterTheFirstOfNovember(newMyCompanyHandBaggageInformationFactory);
        if (myCompanyOneWayAfterTheFirstOfNovember.canHandle(flight)) {
            return myCompanyOneWayAfterTheFirstOfNovember.getFrom(renderLanguage);
        }

        MyCompanyOneWayBeforeTheFirstOfNovember myCompanyOneWayBeforeTheFirstOfNovember =
                new MyCompanyOneWayBeforeTheFirstOfNovember(oldMyCompanyHandBaggageInformationFactory);
        if (myCompanyOneWayBeforeTheFirstOfNovember.canHandle(flight)) {
            return myCompanyOneWayBeforeTheFirstOfNovember.getFrom(renderLanguage);
        }

        MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember =
                new MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember(newMyCompanyHandBaggageInformationFactory);
        if (myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember.canHandle(flight)) {
            return myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember
                    .getFrom(renderLanguage);
        }

        MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember = new
                MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember(oldMyCompanyHandBaggageInformationFactory);
        if (myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember.canHandle(flight)) {
            return myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember.getFrom(renderLanguage);
        }

        return notMyCompanyHandBaggageInformationFactory.make();
    }


    private class MyCompanyOneWayAfterTheFirstOfNovember implements HandBaggageInformationPolicy {

        private final NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory;

        private MyCompanyOneWayAfterTheFirstOfNovember(NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory) {
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

    private class MyCompanyOneWayBeforeTheFirstOfNovember implements HandBaggageInformationPolicy {
        private final OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory;

        private MyCompanyOneWayBeforeTheFirstOfNovember(OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory) {
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

    private class MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember implements HandBaggageInformationPolicy {
        private final NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory;

        public MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember(NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory) {
            this.newMyCompanyHandBaggageInformationFactory = newMyCompanyHandBaggageInformationFactory;
        }

        @Override
        public boolean canHandle(Flight flight) {
            return !flight.isOneWay()
                    && flight.isMyCompany()
                    && (flight.getOutboundDepartureDate().isAfter(FIRST_OF_NOVEMBER)
                        || flight.getReturnDepartureDate().isAfter(FIRST_OF_NOVEMBER)
                    );
        }

        @Override
        public HandBaggageInformation getFrom(String renderLanguage) {
            return this.newMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }
    }

    private class MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember implements HandBaggageInformationPolicy {
        private final OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory;

        private MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember(OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory) {
            this.oldMyCompanyHandBaggageInformationFactory = oldMyCompanyHandBaggageInformationFactory;
        }

        @Override
        public boolean canHandle(Flight flight) {
            return !flight.isOneWay()
                    && flight.isMyCompany()
                    && (!(flight.getOutboundDepartureDate().isAfter(FIRST_OF_NOVEMBER)
                        || flight.getReturnDepartureDate().isAfter(FIRST_OF_NOVEMBER))
                    );
        }

        @Override
        public HandBaggageInformation getFrom(String renderLanguage) {
            return oldMyCompanyHandBaggageInformationFactory.from(renderLanguage);
        }
    }
}
