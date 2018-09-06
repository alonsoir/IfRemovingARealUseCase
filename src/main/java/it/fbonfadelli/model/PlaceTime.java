package it.fbonfadelli.model;

import java.time.LocalDateTime;

public class PlaceTime {
    private Airport airport;
    private LocalDateTime date;
    private String dateUtc;

    public PlaceTime(Airport airport, LocalDateTime date, String dateUtc) {
        this.airport = airport;
        this.date = date;
        this.dateUtc = dateUtc;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDateUtc() {
        return dateUtc;
    }

    public Airport getAirport() {
        return airport;
    }
}
