package it.fbonfadelli.hand_baggage;

import it.fbonfadelli.translation.TranslationRepository;

class OldMyCompanyHandBaggageInformationFactory {
    private static final String MY_COMPANY_BAGGAGE_INFORMATION_LABEL = "customer_area.hand_baggage_policy.label.my_company_id";
    private static final String MY_COMPANY_BAGGAGE_INFORMATION_LINK = "customer_area.hand_baggage_policy.link.my_company_id";

    private TranslationRepository translationRepository;

    public OldMyCompanyHandBaggageInformationFactory(TranslationRepository translationRepository) {
        this.translationRepository = translationRepository;
    }

    public HandBaggageInformation from(String renderLanguage) {
        return new HandBaggageInformation(
                null,
                true,
                "<a target=\"_blank\" href=\"" +
                        translationRepository.retrieve(MY_COMPANY_BAGGAGE_INFORMATION_LINK, renderLanguage) + "\">" + translationRepository.retrieve(MY_COMPANY_BAGGAGE_INFORMATION_LABEL, renderLanguage) + "</a>");
    }
}
