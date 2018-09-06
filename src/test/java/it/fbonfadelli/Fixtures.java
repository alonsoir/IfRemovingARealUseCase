package it.fbonfadelli;

import it.fbonfadelli.model.Airport;
import it.fbonfadelli.model.HopFlight;
import it.fbonfadelli.model.PlaceTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


class Fixtures {

    static PlaceTime placeTime(String placeTimeString) {
        final String[] split = placeTimeString.split("-");
        return new PlaceTime(airport(split[0]), date(split[1]), split[1] + "Z");
    }

    private static LocalDateTime date(String date_yyyyMMdd) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate localDate = LocalDate.parse(date_yyyyMMdd, formatter);
        return localDate.atStartOfDay();
    }

    private static Airport airport(String airportCode) {
        Airport airport = new Airport();
        airport.setCode(airportCode);
        return airport;
    }

    static HopFlight hopFlight(String airlineId) {
        HopFlight hopFlight = new HopFlight();
        hopFlight.setAirlineId(airlineId);
        return hopFlight;
    }
}
