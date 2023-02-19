package it.snapoli.services.insurance.xceptions;

public class BusinessLogicError extends RuntimeException{

    public BusinessLogicError(String message) {
        super(message);
    }
}
