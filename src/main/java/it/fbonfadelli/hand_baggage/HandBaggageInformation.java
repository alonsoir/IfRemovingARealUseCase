package it.fbonfadelli.hand_baggage;

import it.fbonfadelli.model.HandBaggageAlert;

import java.util.Objects;

public class HandBaggageInformation {
    public final HandBaggageAlert alert;
    public final boolean handBaggageAllowed;
    public final String handBaggagePolicy;

    public HandBaggageInformation(HandBaggageAlert alert, boolean handBaggageAllowed, String handBaggagePolicy) {
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
