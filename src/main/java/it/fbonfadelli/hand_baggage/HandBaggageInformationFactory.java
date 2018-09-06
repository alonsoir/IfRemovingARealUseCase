package it.fbonfadelli.hand_baggage;

import it.fbonfadelli.model.Flight;
import it.fbonfadelli.model.Order;
import it.fbonfadelli.translation.TranslationRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class HandBaggageInformationFactory {
    private static final String MY_COMPANY_BAGGAGE_INFORMATION_LABEL = "customer_area.hand_baggage_policy.label.my_company_id";
    private static final String MY_COMPANY_BAGGAGE_INFORMATION_LINK = "customer_area.hand_baggage_policy.link.my_company_id";
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

        if (flight.isOneWay()
                && isMyCompany(flight)
                && flightOutboundDate.isAfter(FIRST_OF_NOVEMBER)) {
            return newMyCompanyHandBaggageInformationFactory.execute(renderLanguage);
        }

        if (flight.isOneWay() && isMyCompany(flight) && !flightOutboundDate.isAfter(FIRST_OF_NOVEMBER)) {
            return oldMyCompanyHandBaggageInformationInfo(translationRepository, renderLanguage);
        }

        if (flight.isOneWay() && !isMyCompany(flight)) {
                return noMyCompanyInformationInfo();
        }

        if (!flight.isOneWay() && isMyCompany(flight) && (outboundDepartureDate.isAfter(FIRST_OF_NOVEMBER)
                        || returnDepartureDate.isAfter(THIRTY_FIRST_OF_OCTOBER))) {
            return newMyCompanyHandBaggageInformationFactory.execute(renderLanguage);
        }

        if (!flight.isOneWay() && isMyCompany(flight) && (!(outboundDepartureDate.isAfter(FIRST_OF_NOVEMBER)
                        || returnDepartureDate.isAfter(THIRTY_FIRST_OF_OCTOBER)))) {
                    return oldMyCompanyHandBaggageInformationInfo(translationRepository, renderLanguage);
        }

        if (!flight.isOneWay() && !isMyCompany(flight)) {
                return noMyCompanyInformationInfo();
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

    private boolean isMyCompany(Flight flight) {
        return flight.getAirlineIds().contains(MY_COMPANY_AIRLINE_ID);
    }


}
