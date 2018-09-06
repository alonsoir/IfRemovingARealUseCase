package it.fbonfadelli.hand_baggage;

import it.fbonfadelli.hand_baggage.factory.NewMyCompanyHandBaggageInformationFactory;
import it.fbonfadelli.hand_baggage.factory.NotMyCompanyHandBaggageInformationFactory;
import it.fbonfadelli.hand_baggage.factory.OldMyCompanyHandBaggageInformationFactory;
import it.fbonfadelli.hand_baggage.policy.MyCompanyOneWayAfterTheFirstOfNovember;
import it.fbonfadelli.hand_baggage.policy.MyCompanyOneWayBeforeTheFirstOfNovember;
import it.fbonfadelli.hand_baggage.policy.MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember;
import it.fbonfadelli.hand_baggage.policy.MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember;
import it.fbonfadelli.model.Flight;
import it.fbonfadelli.model.Order;
import it.fbonfadelli.translation.TranslationRepository;

import java.util.Collections;
import java.util.List;

public class HandBaggageInformationFactory {
    public HandBaggageInformation from(Order order, TranslationRepository translationRepository, String renderLanguage, Integer flightId) {
        Flight flight = order.findFlight(flightId);

        NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory =
                new NewMyCompanyHandBaggageInformationFactory(translationRepository);
        OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory =
                new OldMyCompanyHandBaggageInformationFactory(translationRepository);
        NotMyCompanyHandBaggageInformationFactory notMyCompanyHandBaggageInformationFactory =
                new NotMyCompanyHandBaggageInformationFactory();

        HandBaggageInformationPolicy myCompanyOneWayAfterTheFirstOfNovember =
                new MyCompanyOneWayAfterTheFirstOfNovember(newMyCompanyHandBaggageInformationFactory);
        HandBaggageInformationPolicy myCompanyOneWayBeforeTheFirstOfNovember =
                new MyCompanyOneWayBeforeTheFirstOfNovember(oldMyCompanyHandBaggageInformationFactory);
        HandBaggageInformationPolicy myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember =
                new MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember(newMyCompanyHandBaggageInformationFactory);
        HandBaggageInformationPolicy myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember = new
                MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember(oldMyCompanyHandBaggageInformationFactory);

        List<HandBaggageInformationPolicy> policies = Collections.singletonList(
                myCompanyOneWayAfterTheFirstOfNovember
        );

        for (HandBaggageInformationPolicy policy : policies) {
            if (policy.canHandle(flight)) {
                return policy.getFrom(renderLanguage);
            }
        }

        if (myCompanyOneWayBeforeTheFirstOfNovember.canHandle(flight)) {
            return myCompanyOneWayBeforeTheFirstOfNovember.getFrom(renderLanguage);
        }

        if (myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember.canHandle(flight)) {
            return myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember
                    .getFrom(renderLanguage);
        }

        if (myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember.canHandle(flight)) {
            return myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember.getFrom(renderLanguage);
        }

        return notMyCompanyHandBaggageInformationFactory.make();
    }
}
