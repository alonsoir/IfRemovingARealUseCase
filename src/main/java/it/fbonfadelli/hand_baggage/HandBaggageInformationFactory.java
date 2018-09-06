package it.fbonfadelli.hand_baggage;

import it.fbonfadelli.model.Flight;
import it.fbonfadelli.model.HandBaggageAlert;
import it.fbonfadelli.model.Order;
import it.fbonfadelli.translation.TranslationRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class HandBaggageInformationFactory {
    private static final String MY_COMPANY_BAGGAGE_INFORMATION_LABEL = "customer_area.hand_baggage_policy.label.my_company_id";
    private static final String MY_COMPANY_BAGGAGE_INFORMATION_LINK = "customer_area.hand_baggage_policy.link.my_company_id";
    private static final String MY_COMPANY_NEW_BAGGAGE_INFORMATION_LABEL = "customer_area.new_hand_baggage_policy.label.my_company_id";
    private static final String MY_COMPANY_NEW_BAGGAGE_INFORMATION_LINK = "customer_area.new_hand_baggage_policy.link.my_company_id";
    private static final String CUSTOMER_AREA_CIA_NEW_HAND_LUGGAGE_POLICY_ALERT_TITLE_FR = "customer_area.new_hand_baggage_policy.alert.title.my_company_id";
    private static final String CUSTOMER_AREA_CIA_NEW_HAND_LUGGAGE_POLICY_ALERT_FR = "customer_area.new_hand_baggage_policy.alert.my_company_id";
    private static final String MY_COMPANY_AIRLINE_ID = "MY_COMPANY_AIRLINE_ID";

    public HandBaggageInformation from(Order order, TranslationRepository translationRepository, String renderLanguage, Integer flightId) {
        Flight flight = order.findFlight(flightId);
        if (flight.isOneWay()) {
            LocalDateTime flightOutboundDate = flight.getFirstLeg().getFirstHop().getDeparture().getDate();
            if (isMyCompany(flight)) {
                if (flightOutboundDate.isAfter(LocalDateTime.of(2018, 11, 1, 0, 0, 0))) {
                    return newMyCompanyHandBaggageInformation(translationRepository, renderLanguage);
                }

            }
        }

        if (flight.isOneWay()) {
            LocalDateTime flightOutboundDate = flight.getFirstLeg().getFirstHop().getDeparture().getDate();
            if (isMyCompany(flight)) {
                if (!flightOutboundDate.isAfter(LocalDateTime.of(2018, 11, 1, 0, 0, 0))) {
                    return oldMyCompanyHandBaggageInformationInfo(translationRepository, renderLanguage);
                }
            }
        }

        if (flight.isOneWay()) {
            if (!isMyCompany(flight)) {
                return noMyCompanyInformationInfo();
            }
        }

        if (!flight.isOneWay()) {  //round trip
            LocalDateTime outboundDepartureDate = order.getOutboundDepartureDate();
            LocalDate returnDepartureDate = order.getReturnDepartureDate();
            if (isMyCompany(flight)) {
                if (outboundDepartureDate.isAfter(LocalDateTime.of(2018, 11, 1, 0, 0, 0))
                        || returnDepartureDate.isAfter(LocalDate.of(2018, 10, 31))) {
                    return newMyCompanyHandBaggageInformation(translationRepository, renderLanguage);
                }

            }
        }

        if (!flight.isOneWay()) {  //round trip
            LocalDateTime outboundDepartureDate = order.getOutboundDepartureDate();
            LocalDate returnDepartureDate = order.getReturnDepartureDate();
            if (isMyCompany(flight)) {
                if (!(outboundDepartureDate.isAfter(LocalDateTime.of(2018, 11, 1, 0, 0, 0))
                        || returnDepartureDate.isAfter(LocalDate.of(2018, 10, 31)))) {
                    return oldMyCompanyHandBaggageInformationInfo(translationRepository, renderLanguage);
                }
            }
        }

        if (!flight.isOneWay()) {  //round trip
            if (!isMyCompany(flight)) {
                return noMyCompanyInformationInfo();
            }
        }

        return noMyCompanyInformationInfo();
    }

    private HandBaggageInformation noMyCompanyInformationInfo() {
        return new HandBaggageInformation(
                null,
                true,
                null
        );
    }

    private HandBaggageInformation oldMyCompanyHandBaggageInformationInfo(TranslationRepository translationRepository, String renderLanguage) {
        return new HandBaggageInformation(
                null,
                true,
                "<a target=\"_blank\" href=\"" +
                        translationRepository.retrieve(MY_COMPANY_BAGGAGE_INFORMATION_LINK, renderLanguage) + "\">" + translationRepository.retrieve(MY_COMPANY_BAGGAGE_INFORMATION_LABEL, renderLanguage) + "</a>");
    }

    private HandBaggageInformation newMyCompanyHandBaggageInformation(TranslationRepository translationRepository, String renderLanguage) {
        return new HandBaggageInformation(
                createHandBaggageAlert(translationRepository, renderLanguage),
                false,
                translationRepository.retrieve(MY_COMPANY_NEW_BAGGAGE_INFORMATION_LABEL, renderLanguage).replace("{{link}}", translationRepository.retrieve(MY_COMPANY_NEW_BAGGAGE_INFORMATION_LINK, renderLanguage))
        );
    }

    private boolean isMyCompany(Flight flight) {
        return flight.getAirlineIds().contains(MY_COMPANY_AIRLINE_ID);
    }

    private HandBaggageAlert createHandBaggageAlert(TranslationRepository translationRepository, String renderLanguage) {
        HandBaggageAlert handBaggageAlert = new HandBaggageAlert();
        handBaggageAlert.setTitle(translationRepository.retrieve(CUSTOMER_AREA_CIA_NEW_HAND_LUGGAGE_POLICY_ALERT_TITLE_FR, renderLanguage));
        String message = translationRepository.retrieve(CUSTOMER_AREA_CIA_NEW_HAND_LUGGAGE_POLICY_ALERT_FR, renderLanguage);
        String link = translationRepository.retrieve(MY_COMPANY_NEW_BAGGAGE_INFORMATION_LINK, renderLanguage);
        String messageWithLink = message.replace("{{link}}", link);
        handBaggageAlert.setMessage(messageWithLink);
        return handBaggageAlert;
    }

    public static class HandBaggageInformation {
        public final HandBaggageAlert alert;
        public final boolean handBaggageAllowed;
        public final String handBaggagePolicy;

        HandBaggageInformation(HandBaggageAlert alert, boolean handBaggageAllowed, String handBaggagePolicy) {
            this.alert = alert;
            this.handBaggageAllowed = handBaggageAllowed;
            this.handBaggagePolicy = handBaggagePolicy;
        }

        @Override
        public String toString() {
            return "HandBaggageInformation{" +
                    "alert=" + alert +
                    ", handBaggageAllowed=" + handBaggageAllowed +
                    ", handBaggagePolicy='" + handBaggagePolicy + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HandBaggageInformation that = (HandBaggageInformation) o;
            return handBaggageAllowed == that.handBaggageAllowed &&
                    Objects.equals(alert, that.alert) &&
                    Objects.equals(handBaggagePolicy, that.handBaggagePolicy);
        }

        @Override
        public int hashCode() {
            return 0;
        }
    }


}
