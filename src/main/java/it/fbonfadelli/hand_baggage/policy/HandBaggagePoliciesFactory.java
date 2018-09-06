package it.fbonfadelli.hand_baggage.policy;

import it.fbonfadelli.hand_baggage.HandBaggageInformationPolicy;
import it.fbonfadelli.hand_baggage.factory.NewMyCompanyHandBaggageInformationFactory;
import it.fbonfadelli.hand_baggage.factory.OldMyCompanyHandBaggageInformationFactory;
import it.fbonfadelli.translation.TranslationRepository;

import java.util.Arrays;
import java.util.List;

public class HandBaggagePoliciesFactory {
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
