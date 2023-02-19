package it.snapoli.services.insurance.xceptions;

public class ForeignKeyException extends RuntimeException{
    public ForeignKeyException(String message) {
        super(message);
    }
}
