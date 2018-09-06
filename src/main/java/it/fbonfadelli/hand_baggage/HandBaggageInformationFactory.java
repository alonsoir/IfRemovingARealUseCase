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

import java.util.Arrays;
import java.util.List;

public class HandBaggageInformationFactory {
    public HandBaggageInformation from(Order order, TranslationRepository translationRepository, String renderLanguage, Integer flightId) {
        Flight flight = order.findFlight(flightId);

        NotMyCompanyHandBaggageInformationFactory notMyCompanyHandBaggageInformationFactory =
                new NotMyCompanyHandBaggageInformationFactory();

        List<HandBaggageInformationPolicy> policies = new HandBaggagePoliciesFactory().make(translationRepository);

        return policies.stream()
                .filter(policy -> policy.canHandle(flight))
                .findFirst()
                .map(policy -> policy.getFrom(renderLanguage))
                .orElse(notMyCompanyHandBaggageInformationFactory.make());
    }

    private static class HandBaggagePoliciesFactory {
        public List<HandBaggageInformationPolicy> make(TranslationRepository translationRepository) {
            NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory =
                    new NewMyCompanyHandBaggageInformationFactory(translationRepository);
            OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory =
                    new OldMyCompanyHandBaggageInformationFactory(translationRepository);

            HandBaggageInformationPolicy myCompanyOneWayAfterTheFirstOfNovember =
                    new MyCompanyOneWayAfterTheFirstOfNovember(newMyCompanyHandBaggageInformationFactory);
            HandBaggageInformationPolicy myCompanyOneWayBeforeTheFirstOfNovember =
                    new MyCompanyOneWayBeforeTheFirstOfNovember(oldMyCompanyHandBaggageInformationFactory);
            HandBaggageInformationPolicy myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember =
                    new MyCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember(newMyCompanyHandBaggageInformationFactory);
            HandBaggageInformationPolicy myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember = new
                    MyCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember(oldMyCompanyHandBaggageInformationFactory);

            return Arrays.asList(
                    myCompanyOneWayAfterTheFirstOfNovember,
                    myCompanyOneWayBeforeTheFirstOfNovember,
                    myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember,
                    myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember
            );
        }
    }
}
