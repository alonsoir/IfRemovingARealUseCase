package it.fbonfadelli.hand_baggage;

import it.fbonfadelli.FlightBuilder;
import it.fbonfadelli.OrderBuilder;
import it.fbonfadelli.hand_baggage.factory.NotMyCompanyHandBaggageInformationFactory;
import it.fbonfadelli.hand_baggage.policy.HandBaggagePoliciesFactory;
import it.fbonfadelli.model.*;
import it.fbonfadelli.translation.TranslationRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static it.fbonfadelli.HopBuilder.aHop;
import static it.fbonfadelli.LegBuilder.aLeg;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class HandBaggageInformationFactoryTest {

    private static final String A_RENDER_LANGUAGE = "::a_render_language::";
    private static final String MY_COMPANY_AIRLINE_ID = "MY_COMPANY_AIRLINE_ID";
    private static final String MY_COMPANY_NEW_HAND_BAGGAGE_LINK = "::the_new_link::";
    private static final String MY_COMPANY_OLD_HAND_BAGGAGE_LINK = "::the_old_link::";
    private static final String MY_COMPANY_NEW_HAND_BAGGAGE_LINK_MESSAGE = "new hand luggage rules.";
    private static final String MY_COMPANY_NEW_HAND_BAGGAGE_POLICY = "Small carry-on bag included. For more information, please check the <a href=\"" + MY_COMPANY_NEW_HAND_BAGGAGE_LINK + "\" target=\"_blank\" rel=\"noopener\">" + MY_COMPANY_NEW_HAND_BAGGAGE_LINK_MESSAGE + "</a>.";
    private static final String MY_COMPANY_OLD_HAND_BAGGAGE_LINK_MESSAGE = "View hand luggage rules for MyCompany";
    private static final String MY_COMPANY_OLD_HAND_BAGGAGE_POLICY = "<a target=\"_blank\" href=\"" + MY_COMPANY_OLD_HAND_BAGGAGE_LINK + "\">" + MY_COMPANY_OLD_HAND_BAGGAGE_LINK_MESSAGE + "</a>";

    private HandBaggageInformationFactory handBaggageInformationFactory;
    private TranslationRepository translationRepository;

    @Before
    public void setUp() {
        translationRepository = Mockito.mock(TranslationRepository.class);
        handBaggageInformationFactory = new HandBaggageInformationFactory(new HandBaggagePoliciesFactory().make(translationRepository), new NotMyCompanyHandBaggageInformationFactory());

        when(translationRepository.retrieve("customer_area.hand_baggage_policy.label.my_company_id", A_RENDER_LANGUAGE))
                .thenReturn(MY_COMPANY_OLD_HAND_BAGGAGE_LINK_MESSAGE);
        when(translationRepository.retrieve("customer_area.hand_baggage_policy.link.my_company_id", A_RENDER_LANGUAGE))
                .thenReturn(MY_COMPANY_OLD_HAND_BAGGAGE_LINK);
        when(translationRepository.retrieve("customer_area.new_hand_baggage_policy.label.my_company_id", A_RENDER_LANGUAGE))
                .thenReturn("Small carry-on bag included. For more information, please check the {{link}}.");
        when(translationRepository.retrieve("customer_area.new_hand_baggage_policy.link.my_company_id", A_RENDER_LANGUAGE))
                .thenReturn("<a href=\"" + MY_COMPANY_NEW_HAND_BAGGAGE_LINK + "\" target=\"_blank\" rel=\"noopener\">" + MY_COMPANY_NEW_HAND_BAGGAGE_LINK_MESSAGE + "</a>");
        when(translationRepository.retrieve("customer_area.new_hand_baggage_policy.alert.title.my_company_id", A_RENDER_LANGUAGE))
                .thenReturn("Update to  MyCompany's baggage policy");
        when(translationRepository.retrieve("customer_area.new_hand_baggage_policy.alert.my_company_id", A_RENDER_LANGUAGE))
                .thenReturn("We strongly recommend that you check the {{link}}.");
    }

    @Test
    public void noMyCompanyOneWay() {
        final Flight flight = FlightBuilder.aFlight().addLeg(aLeg().withHops(aHop().withAirlineId("::not_my_company::").build()).build()).build();
        final Order order = OrderBuilder.anOrder().withFlights(flight).build();

        HandBaggageInformation handBaggageInformation = handBaggageInformationFactory.from(order, A_RENDER_LANGUAGE, 1);

        assertThat(handBaggageInformation, is(
                new HandBaggageInformation(
                        null,
                        true,
                        null
                )
        ));
    }

    @Test
    public void noMyCompanyRoundTrip() {
        final Flight flight = FlightBuilder.aFlight()
                .addLeg(aLeg().withHops(aHop().withAirlineId("::not_my_company::").build()).build())
                .addLeg(aLeg().withHops(aHop().withAirlineId("::not_my_company::").build()).build())
                .build();
        final Order order = OrderBuilder.anOrder().withFlights(flight).build();

        HandBaggageInformation handBaggageInformation = handBaggageInformationFactory.from(order, A_RENDER_LANGUAGE, 1);

        assertThat(handBaggageInformation, is(
                new HandBaggageInformation(
                        null,
                        true,
                        null
                )
        ));
    }

    @Test
    public void noMyCompanyDoubleOneWay() {
        final Flight outboundFlight = FlightBuilder.aFlight()
                .addLeg(aLeg().withHops(aHop().withAirlineId("::not_my_company::").build()).build())
                .build();
        final Flight returnFlight = FlightBuilder.aFlight().withFlightId(2)
                .addLeg(aLeg().withHops(aHop().withAirlineId("::not_my_company::").build()).build())
                .build();
        final Order order = OrderBuilder.anOrder().withFlights(outboundFlight, returnFlight).build();

        HandBaggageInformation handBaggageInformationOfFirstFlight = handBaggageInformationFactory.from(order, A_RENDER_LANGUAGE, 1);
        assertThat(handBaggageInformationOfFirstFlight, is(
                new HandBaggageInformation(
                        null,
                        true,
                        null
                )
        ));

        HandBaggageInformation handBaggageInformationOfSecondFlight = handBaggageInformationFactory.from(order, A_RENDER_LANGUAGE, 2);
        assertThat(handBaggageInformationOfSecondFlight, is(
                new HandBaggageInformation(
                        null,
                        true,
                        null
                )
        ));
    }

    @Test
    public void myCompanyOneWayWithOutboundBeforeTheFirstOfNovember2018() {
        final Hop departureHop = aHop()
                .withAirlineId(MY_COMPANY_AIRLINE_ID)
                .withDepartureTime(LocalDateTime.of(2018, 10, 8, 10, 0, 0)).build();

        final Flight outboundFlight = FlightBuilder.aFlight().withAirline(MY_COMPANY_AIRLINE_ID)
                .addLeg(aLeg().withHops(departureHop).build()).build();

        final Order order = OrderBuilder.anOrder().withFlights(outboundFlight).build();

        HandBaggageInformation handBaggageInformation = handBaggageInformationFactory.from(order, A_RENDER_LANGUAGE, 1);

        assertThat(handBaggageInformation, is(
                new HandBaggageInformation(
                        null,
                        true,
                        MY_COMPANY_OLD_HAND_BAGGAGE_POLICY
                )
        ));
    }

    @Test
    public void myCompanyOneWayWithOutboundAfterTheFirstOfNovember2018() {
        final Hop departureHop = aHop()
                .withAirlineId(MY_COMPANY_AIRLINE_ID)
                .withDepartureTime(LocalDateTime.of(2018, 11, 1, 10, 0, 0)).build();

        final Flight outboundFlight = FlightBuilder.aFlight().withAirline(MY_COMPANY_AIRLINE_ID)
                .addLeg(aLeg().withHops(departureHop).build()).build();

        final Order order = OrderBuilder.anOrder().withFlights(outboundFlight).build();

        HandBaggageInformation handBaggageInformation = handBaggageInformationFactory.from(order, A_RENDER_LANGUAGE, 1);

        assertThat(handBaggageInformation, is(
                new HandBaggageInformation(
                        handBaggageAlert(),
                        false,
                        MY_COMPANY_NEW_HAND_BAGGAGE_POLICY
                )
        ));
    }

    private HandBaggageAlert handBaggageAlert() {
        HandBaggageAlert handBaggageAlert = new HandBaggageAlert();
        handBaggageAlert.setTitle("Update to  MyCompany's baggage policy");
        handBaggageAlert.setMessage("We strongly recommend that you check the <a href=\"" + MY_COMPANY_NEW_HAND_BAGGAGE_LINK + "\" target=\"_blank\" rel=\"noopener\">" + MY_COMPANY_NEW_HAND_BAGGAGE_LINK_MESSAGE + "</a>.");
        return handBaggageAlert;
    }

    @Test
    public void myCompanyRoundTripWithOutboundAndReturnBeforeTheFirstOfNovember2018() {
        final Hop departureHop = aHop()
                .withAirlineId(MY_COMPANY_AIRLINE_ID)
                .withDepartureTime(LocalDateTime.of(2018, 10, 8, 10, 0, 0)).build();
        Leg outboundLeg = aLeg().withHops(departureHop).build();

        final Hop returnHop = aHop()
                .withAirlineId(MY_COMPANY_AIRLINE_ID)
                .withDepartureTime(LocalDateTime.of(2018, 10, 28, 10, 0, 0)).build();
        Leg returnLeg = aLeg().withHops(returnHop).build();

        final Flight flight = FlightBuilder.aFlight().withAirline(MY_COMPANY_AIRLINE_ID)
                .addLeg(outboundLeg)
                .addLeg(returnLeg)
                .build();

        final Order order = OrderBuilder.anOrder().withFlights(flight).build();

        HandBaggageInformation handBaggageInformationOfFirstFlight = handBaggageInformationFactory.from(order, A_RENDER_LANGUAGE, 1);
        assertThat(handBaggageInformationOfFirstFlight, is(
                new HandBaggageInformation(
                        null,
                        true,
                        MY_COMPANY_OLD_HAND_BAGGAGE_POLICY
                )
        ));
    }

    @Test
    public void myCompanyRoundTripWithOnlyReturnAfterTheFirstOfNovember2018() {
        final Hop departureHop = aHop()
                .withAirlineId(MY_COMPANY_AIRLINE_ID)
                .withDepartureTime(LocalDateTime.of(2018, 10, 8, 10, 0, 0)).build();
        Leg outboundLeg = aLeg().withHops(departureHop).build();

        final Hop returnHop = aHop()
                .withAirlineId(MY_COMPANY_AIRLINE_ID)
                .withDepartureTime(LocalDateTime.of(2018, 11, 1, 10, 0, 0)).build();
        Leg returnLeg = aLeg().withHops(returnHop).build();

        final Flight flight = FlightBuilder.aFlight().withAirline(MY_COMPANY_AIRLINE_ID)
                .addLeg(outboundLeg)
                .addLeg(returnLeg)
                .build();

        final Order order = OrderBuilder.anOrder().withFlights(flight).build();

        HandBaggageInformation handBaggageInformationOfFirstFlight = handBaggageInformationFactory.from(order, A_RENDER_LANGUAGE, 1);
        assertThat(handBaggageInformationOfFirstFlight, is(
                new HandBaggageInformation(
                        handBaggageAlert(),
                        false,
                        MY_COMPANY_NEW_HAND_BAGGAGE_POLICY
                )
        ));
    }

    @Test
    public void myCompanyRoundTripWithBothOutboundAndReturnAfterTheFirstOfNovember2018() {
        final Hop departureHop = aHop()
                .withAirlineId(MY_COMPANY_AIRLINE_ID)
                .withDepartureTime(LocalDateTime.of(2018, 11, 1, 10, 0, 0)).build();
        Leg outboundLeg = aLeg().withHops(departureHop).build();

        final Hop returnHop = aHop()
                .withAirlineId(MY_COMPANY_AIRLINE_ID)
                .withDepartureTime(LocalDateTime.of(2018, 12, 1, 10, 0, 0)).build();
        Leg returnLeg = aLeg().withHops(returnHop).build();

        final Flight flight = FlightBuilder.aFlight().withAirline(MY_COMPANY_AIRLINE_ID)
                .addLeg(outboundLeg)
                .addLeg(returnLeg)
                .build();

        final Order order = OrderBuilder.anOrder().withFlights(flight).build();

        HandBaggageInformation handBaggageInformationOfFirstFlight = handBaggageInformationFactory.from(order, A_RENDER_LANGUAGE, 1);
        assertThat(handBaggageInformationOfFirstFlight, is(
                new HandBaggageInformation(
                        handBaggageAlert(),
                        false,
                        MY_COMPANY_NEW_HAND_BAGGAGE_POLICY
                )
        ));
    }

    @Test
    public void myCompanyDoubleOneWayWithOutboundAndReturnBeforeTheFirstOfNovember2018() {
        final Hop departureHop = aHop()
                .withAirlineId(MY_COMPANY_AIRLINE_ID)
                .withDepartureTime(LocalDateTime.of(2018, 10, 8, 10, 0, 0)).build();
        final Flight outboundFlight = FlightBuilder.aFlight().withAirline(MY_COMPANY_AIRLINE_ID)
                .addLeg(aLeg().withHops(departureHop).build()).build();

        final Hop returnHop = aHop()
                .withAirlineId(MY_COMPANY_AIRLINE_ID)
                .withDepartureTime(LocalDateTime.of(2018, 10, 28, 10, 0, 0)).build();
        final Flight returnFlight = FlightBuilder.aFlight().withAirline(MY_COMPANY_AIRLINE_ID).withFlightId(2)
                .addLeg(aLeg().withHops(returnHop).build()).build();

        final Order order = OrderBuilder.anOrder().withFlights(outboundFlight, returnFlight).build();

        HandBaggageInformation handBaggageInformationOfFirstFlight = handBaggageInformationFactory.from(order, A_RENDER_LANGUAGE, 1);
        assertThat(handBaggageInformationOfFirstFlight, is(
                new HandBaggageInformation(
                        null,
                        true,
                        MY_COMPANY_OLD_HAND_BAGGAGE_POLICY
                )
        ));

        HandBaggageInformation handBaggageInformationOfSecondFlight = handBaggageInformationFactory.from(order, A_RENDER_LANGUAGE, 2);
        assertThat(handBaggageInformationOfSecondFlight, is(
                new HandBaggageInformation(
                        null,
                        true,
                        MY_COMPANY_OLD_HAND_BAGGAGE_POLICY
                )
        ));
    }

    @Test
    public void myCompanyDoubleOneWayOnlyOutboundBeforeTheFirstOfNovember2018() {
        final Hop departureHop = aHop()
                .withAirlineId(MY_COMPANY_AIRLINE_ID)
                .withDepartureTime(LocalDateTime.of(2018, 10, 8, 10, 0, 0)).build();
        final Flight outboundFlight = FlightBuilder.aFlight().withAirline(MY_COMPANY_AIRLINE_ID)
                .addLeg(aLeg().withHops(departureHop).build()).build();

        final Hop returnHop = aHop()
                .withAirlineId(MY_COMPANY_AIRLINE_ID)
                .withDepartureTime(LocalDateTime.of(2018, 11, 1, 10, 0, 0)).build();
        final Flight returnFlight = FlightBuilder.aFlight().withAirline(MY_COMPANY_AIRLINE_ID).withFlightId(2)
                .addLeg(aLeg().withHops(returnHop).build()).build();

        final Order order = OrderBuilder.anOrder().withFlights(outboundFlight, returnFlight).build();

        HandBaggageInformation handBaggageInformationOfFirstFlight = handBaggageInformationFactory.from(order, A_RENDER_LANGUAGE, 1);
        assertThat(handBaggageInformationOfFirstFlight, is(
                new HandBaggageInformation(
                        null,
                        true,
                        MY_COMPANY_OLD_HAND_BAGGAGE_POLICY
                )
        ));

        HandBaggageInformation handBaggageInformationOfSecondFlight = handBaggageInformationFactory.from(order, A_RENDER_LANGUAGE, 2);
        assertThat(handBaggageInformationOfSecondFlight, is(
                new HandBaggageInformation(
                        handBaggageAlert(),
                        false,
                        MY_COMPANY_NEW_HAND_BAGGAGE_POLICY
                )
        ));
    }

    @Test
    public void myCompanyDoubleOneWayWithBothOutboundAndReturnAfterTheFirstOfNovember2018() {
        final Hop departureHop = aHop()
                .withAirlineId(MY_COMPANY_AIRLINE_ID)
                .withDepartureTime(LocalDateTime.of(2018, 11, 1, 10, 0, 0)).build();
        final Flight outboundFlight = FlightBuilder.aFlight().withAirline(MY_COMPANY_AIRLINE_ID)
                .addLeg(aLeg().withHops(departureHop).build()).build();

        final Hop returnHop = aHop()
                .withAirlineId(MY_COMPANY_AIRLINE_ID)
                .withDepartureTime(LocalDateTime.of(2018, 11, 3, 10, 0, 0)).build();
        final Flight returnFlight = FlightBuilder.aFlight().withAirline(MY_COMPANY_AIRLINE_ID).withFlightId(2)
                .addLeg(aLeg().withHops(returnHop).build()).build();

        final Order order = OrderBuilder.anOrder().withFlights(outboundFlight, returnFlight).build();

        HandBaggageInformation handBaggageInformationOfFirstFlight = handBaggageInformationFactory.from(order, A_RENDER_LANGUAGE, 1);
        assertThat(handBaggageInformationOfFirstFlight, is(
                new HandBaggageInformation(
                        handBaggageAlert(),
                        false,
                        MY_COMPANY_NEW_HAND_BAGGAGE_POLICY
                )
        ));

        HandBaggageInformation handBaggageInformationOfSecondFlight = handBaggageInformationFactory.from(order, A_RENDER_LANGUAGE, 2);
        assertThat(handBaggageInformationOfSecondFlight, is(
                new HandBaggageInformation(
                        handBaggageAlert(),
                        false,
                        MY_COMPANY_NEW_HAND_BAGGAGE_POLICY
                )
        ));
    }


    @Test
    public void doubleOneWayMyCompanyOnlyOutboundAfterTheFirstOfNovember2018() {
        final Hop departureHop = aHop()
                .withAirlineId(MY_COMPANY_AIRLINE_ID)
                .withDepartureTime(LocalDateTime.of(2018, 11, 1, 10, 0, 0)).build();
        final Flight outboundFlight = FlightBuilder.aFlight().withAirline(MY_COMPANY_AIRLINE_ID)
                .addLeg(aLeg().withHops(departureHop).build()).build();

        final Hop returnHop = aHop()
                .withDepartureTime(LocalDateTime.of(2018, 11, 3, 10, 0, 0)).build();
        final Flight returnFlight = FlightBuilder.aFlight().withFlightId(2)
                .addLeg(aLeg().withHops(returnHop).build()).build();

        final Order order = OrderBuilder.anOrder().withFlights(outboundFlight, returnFlight).build();

        HandBaggageInformation handBaggageInformationOfFirstFlight = handBaggageInformationFactory.from(order, A_RENDER_LANGUAGE, 1);
        assertThat(handBaggageInformationOfFirstFlight, is(
                new HandBaggageInformation(
                        handBaggageAlert(),
                        false,
                        MY_COMPANY_NEW_HAND_BAGGAGE_POLICY
                )
        ));

        HandBaggageInformation handBaggageInformationOfSecondFlight = handBaggageInformationFactory.from(order, A_RENDER_LANGUAGE, 2);
        assertThat(handBaggageInformationOfSecondFlight, is(
                new HandBaggageInformation(
                        null,
                        true,
                        null
                )
        ));
    }

    @Test
    public void doubleOneWayMyCompanyOnlyOutboundBeforeTheFirstOfNovember2018() {
        final Hop departureHop = aHop()
                .withAirlineId(MY_COMPANY_AIRLINE_ID)
                .withDepartureTime(LocalDateTime.of(2018, 10, 31, 10, 0, 0)).build();
        final Flight outboundFlight = FlightBuilder.aFlight().withAirline(MY_COMPANY_AIRLINE_ID)
                .addLeg(aLeg().withHops(departureHop).build()).build();

        final Hop returnHop = aHop()
                .withDepartureTime(LocalDateTime.of(2018, 11, 3, 10, 0, 0)).build();
        final Flight returnFlight = FlightBuilder.aFlight().withFlightId(2)
                .addLeg(aLeg().withHops(returnHop).build()).build();

        final Order order = OrderBuilder.anOrder().withFlights(outboundFlight, returnFlight).build();

        HandBaggageInformation handBaggageInformationOfFirstFlight = handBaggageInformationFactory.from(order, A_RENDER_LANGUAGE, 1);
        assertThat(handBaggageInformationOfFirstFlight, is(
                new HandBaggageInformation(
                        null,
                        true,
                        MY_COMPANY_OLD_HAND_BAGGAGE_POLICY
                )
        ));

        HandBaggageInformation handBaggageInformationOfSecondFlight = handBaggageInformationFactory.from(order, A_RENDER_LANGUAGE, 2);
        assertThat(handBaggageInformationOfSecondFlight, is(
                new HandBaggageInformation(
                        null,
                        true,
                        null
                )
        ));
    }

    @Test
    public void doubleOneWayMyCompanyOnlyReturnAfterTheFirstOfNovember2018() {
        final Hop outboundHop = aHop()
                .withDepartureTime(LocalDateTime.of(2018, 11, 3, 10, 0, 0)).build();
        final Flight outboundFlight = FlightBuilder.aFlight()
                .addLeg(aLeg().withHops(outboundHop).build()).build();

        final Hop returnHop = aHop()
                .withAirlineId(MY_COMPANY_AIRLINE_ID)
                .withDepartureTime(LocalDateTime.of(2018, 11, 4, 10, 0, 0)).build();
        final Flight returnFlight = FlightBuilder.aFlight().withAirline(MY_COMPANY_AIRLINE_ID).withFlightId(2)
                .addLeg(aLeg().withHops(returnHop).build()).build();

        final Order order = OrderBuilder.anOrder().withFlights(outboundFlight, returnFlight).build();

        HandBaggageInformation handBaggageInformationOfFirstFlight = handBaggageInformationFactory.from(order, A_RENDER_LANGUAGE, 1);
        assertThat(handBaggageInformationOfFirstFlight, is(
                new HandBaggageInformation(
                        null,
                        true,
                        null
                )
        ));

        HandBaggageInformation handBaggageInformationOfSecondFlight = handBaggageInformationFactory.from(order, A_RENDER_LANGUAGE, 2);
        assertThat(handBaggageInformationOfSecondFlight, is(
                new HandBaggageInformation(
                        handBaggageAlert(),
                        false,
                        MY_COMPANY_NEW_HAND_BAGGAGE_POLICY
                )
        ));
    }

    @Test
    public void doubleOneWayMyCompanyOnlyReturnBeforeTheFirstOfNovember2018() {
        final Hop outboundHop = aHop()
                .withDepartureTime(LocalDateTime.of(2018, 10, 3, 10, 0, 0)).build();
        final Flight outboundFlight = FlightBuilder.aFlight()
                .addLeg(aLeg().withHops(outboundHop).build()).build();

        final Hop returnHop = aHop()
                .withAirlineId(MY_COMPANY_AIRLINE_ID)
                .withDepartureTime(LocalDateTime.of(2018, 10, 31, 10, 0, 0)).build();
        final Flight returnFlight = FlightBuilder.aFlight().withAirline(MY_COMPANY_AIRLINE_ID).withFlightId(2)
                .addLeg(aLeg().withHops(returnHop).build()).build();

        final Order order = OrderBuilder.anOrder().withFlights(outboundFlight, returnFlight).build();

        HandBaggageInformation handBaggageInformationOfFirstFlight = handBaggageInformationFactory.from(order, A_RENDER_LANGUAGE, 1);
        assertThat(handBaggageInformationOfFirstFlight, is(
                new HandBaggageInformation(
                        null,
                        true,
                        null
                )
        ));

        HandBaggageInformation handBaggageInformationOfSecondFlight = handBaggageInformationFactory.from(order, A_RENDER_LANGUAGE, 2);
        assertThat(handBaggageInformationOfSecondFlight, is(
                new HandBaggageInformation(
                        null,
                        true,
                        MY_COMPANY_OLD_HAND_BAGGAGE_POLICY
                )
        ));
    }
}