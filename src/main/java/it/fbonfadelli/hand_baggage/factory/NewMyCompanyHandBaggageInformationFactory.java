package it.fbonfadelli.hand_baggage.factory;

import it.fbonfadelli.hand_baggage.HandBaggageInformation;
import it.fbonfadelli.model.HandBaggageAlert;
import it.fbonfadelli.translation.TranslationRepository;

public class NewMyCompanyHandBaggageInformationFactory {
    private static final String MY_COMPANY_NEW_BAGGAGE_INFORMATION_LABEL = "customer_area.new_hand_baggage_policy.label.my_company_id";
    private static final String MY_COMPANY_NEW_BAGGAGE_INFORMATION_LINK = "customer_area.new_hand_baggage_policy.link.my_company_id";
    private static final String CUSTOMER_AREA_CIA_NEW_HAND_LUGGAGE_POLICY_ALERT_TITLE = "customer_area.new_hand_baggage_policy.alert.title.my_company_id";
    private static final String CUSTOMER_AREA_CIA_NEW_HAND_LUGGAGE_POLICY_ALERT = "customer_area.new_hand_baggage_policy.alert.my_company_id";

    private TranslationRepository translationRepository;

    public NewMyCompanyHandBaggageInformationFactory(TranslationRepository translationRepository) {
        this.translationRepository = translationRepository;
    }

    public HandBaggageInformation from(String renderLanguage) {
        return new HandBaggageInformation(
                createHandBaggageAlert(translationRepository, renderLanguage),
                false,
                translationRepository.retrieve(MY_COMPANY_NEW_BAGGAGE_INFORMATION_LABEL, renderLanguage).replace("{{link}}", translationRepository.retrieve(MY_COMPANY_NEW_BAGGAGE_INFORMATION_LINK, renderLanguage))
        );
    }

    private HandBaggageAlert createHandBaggageAlert(TranslationRepository translationRepository, String renderLanguage) {
        HandBaggageAlert handBaggageAlert = new HandBaggageAlert();
        handBaggageAlert.setTitle(translationRepository.retrieve(CUSTOMER_AREA_CIA_NEW_HAND_LUGGAGE_POLICY_ALERT_TITLE, renderLanguage));
        String message = translationRepository.retrieve(CUSTOMER_AREA_CIA_NEW_HAND_LUGGAGE_POLICY_ALERT, renderLanguage);
        String link = translationRepository.retrieve(MY_COMPANY_NEW_BAGGAGE_INFORMATION_LINK, renderLanguage);
        String messageWithLink = message.replace("{{link}}", link);
        handBaggageAlert.setMessage(messageWithLink);
        return handBaggageAlert;
    }
}
