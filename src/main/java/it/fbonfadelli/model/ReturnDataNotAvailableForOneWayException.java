package it.fbonfadelli.model;

class ReturnDataNotAvailableForOneWayException extends RuntimeException {
    ReturnDataNotAvailableForOneWayException() {
        super("One way has no return");
    }
}
