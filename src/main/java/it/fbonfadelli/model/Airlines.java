package it.fbonfadelli.model;

public class Airlines {
    private String marketingAirline;
    private String operatingAirline;
    private String platingCarrier;

    public void setMarketingAirline(String marketingAirline) {
        this.marketingAirline = marketingAirline;
    }

    @Override
    public String toString() {
        return "Airlines{" +
                "marketingAirline='" + marketingAirline + '\'' +
                ", operatingAirline='" + operatingAirline + '\'' +
                ", platingCarrier='" + platingCarrier + '\'' +
                '}';
    }
}