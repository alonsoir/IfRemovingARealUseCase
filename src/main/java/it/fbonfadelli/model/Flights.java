package it.fbonfadelli.model;

import java.util.ArrayList;
import java.util.List;

public class Flights extends ArrayList<Flight> {
    public Flights(List<Flight> flights) {
        super(flights);
    }

    Flights() {
        super();
    }
}
