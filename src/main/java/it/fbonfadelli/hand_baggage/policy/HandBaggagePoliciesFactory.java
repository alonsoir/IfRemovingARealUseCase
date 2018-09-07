package it.fbonfadelli.hand_baggage.policy;

import it.fbonfadelli.hand_baggage.HandBaggageInformationPolicy;
import it.fbonfadelli.hand_baggage.factory.NewMyCompanyHandBaggageInformationFactory;
import it.fbonfadelli.hand_baggage.factory.OldMyCompanyHandBaggageInformationFactory;
import it.fbonfadelli.translation.TranslationRepository;

import java.util.Arrays;
import java.util.List;

public class HandBaggagePoliciesFactory {
    public static List<HandBaggageInformationPolicy> make(TranslationRepository translationRepository) {
        NewMyCompanyHandBaggageInformationFactory newMyCompanyHandBaggageInformationFactory =
                new NewMyCompanyHandBaggageInformationFactory(translationRepository);
        OldMyCompanyHandBaggageInformationFactory oldMyCompanyHandBaggageInformationFactory =
                new OldMyCompanyHandBaggageInformationFactory(translationRepository);

        HandBaggageInformationPolicy myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember =
                new MyCompanyAtLeastOneDepartureAfterTheFirstOfNovember(newMyCompanyHandBaggageInformationFactory);
        HandBaggageInformationPolicy myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember = new
                MyCompanyAllDeparturesBeforeTheFirstOfNovember(oldMyCompanyHandBaggageInformationFactory);

        return Arrays.asList(
                myCompanyRoundTripAtLeastOneDepartureAfterTheFirstOfNovember,
                myCompanyRoundTripAllDeparturesBeforeTheFirstOfNovember
        );
    }
}
