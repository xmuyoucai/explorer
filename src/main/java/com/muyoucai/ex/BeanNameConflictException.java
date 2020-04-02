package com.muyoucai.ex;

public class BeanNameConflictException extends CustomException {

    public BeanNameConflictException() {
    }

    public BeanNameConflictException(String message) {
        super(message);
    }

    public BeanNameConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanNameConflictException(Throwable cause) {
        super(cause);
    }

    public BeanNameConflictException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
