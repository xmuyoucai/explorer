package com.muyoucai.ex;

public class FieldInjectorFailedException extends CustomException {

    public FieldInjectorFailedException() {
    }

    public FieldInjectorFailedException(String message) {
        super(message);
    }

    public FieldInjectorFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public FieldInjectorFailedException(Throwable cause) {
        super(cause);
    }

    public FieldInjectorFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
