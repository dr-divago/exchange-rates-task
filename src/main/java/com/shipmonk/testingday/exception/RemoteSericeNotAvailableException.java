package com.shipmonk.testingday.exception;

public class RemoteSericeNotAvailableException extends RuntimeException {
    public RemoteSericeNotAvailableException(String errorBody) {
        super(errorBody);
    }

    public RemoteSericeNotAvailableException(Throwable failure) {
        super(failure);
    }
}
