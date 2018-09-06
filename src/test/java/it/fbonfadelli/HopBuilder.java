package it.fbonfadelli;

import it.fbonfadelli.model.Hop;
import it.fbonfadelli.model.PlaceTime;

import java.time.LocalDateTime;

public class HopBuilder
{
  private String airlineId = "U2";
  private PlaceTime departure = Fixtures.placeTime("MXP-20141231");
  private PlaceTime arrival = Fixtures.placeTime("MAD-20150101");

  private HopBuilder()
  {
  }

  public static HopBuilder aHop()
  {
    return new HopBuilder();
  }

  public Hop build()
  {
    Hop hop = new Hop();

    hop.setDeparture(departure);
    hop.setArrival(arrival);
    hop.setHopFlight(Fixtures.hopFlight(airlineId));
    int id = 1;
    hop.setHopId(id);
    int flightId = 1;
    hop.setFlightId(flightId);
    int legId = 1;
    hop.setLegId(legId);

    return hop;
  }

  public HopBuilder withAirlineId(String airlineId)
  {
    this.airlineId = airlineId;
    return this;
  }

  public HopBuilder withDepartureTime(LocalDateTime date)
  {
    this.departure.setDate(date);
    return this;
  }
}
