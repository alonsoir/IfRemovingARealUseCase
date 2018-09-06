package it.fbonfadelli.hand_baggage;

class NotMyCompanyHandBaggageInformationFactory {
    public HandBaggageInformation make() {
        return new HandBaggageInformation(
                null,
                true,
                null
        );
    }
}
