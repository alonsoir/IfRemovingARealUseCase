package it.fbonfadelli.hand_baggage;

import it.fbonfadelli.hand_baggage.factory.NewMyCompanyHandBaggageInformationFactory;
import it.fbonfadelli.hand_baggage.factory.NotMyCompanyHandBaggageInformationFactory;
import it.fbonfadelli.hand_baggage.factory.OldMyCompanyHandBaggageInformationFactory;
import it.fbonfadelli.model.Flight;
import it.fbonfadelli.model.Order;
import it.fbonfadelli.translation.TranslationRepository;

public class HandBaggageInformationFactory {
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


}
