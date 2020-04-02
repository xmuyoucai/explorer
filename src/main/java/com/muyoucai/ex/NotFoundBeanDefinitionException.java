package com.muyoucai.ex;

public class NotFoundBeanDefinitionException extends CustomException {

    public NotFoundBeanDefinitionException() {
    }

    public NotFoundBeanDefinitionException(String message) {
        super(message);
    }

    public NotFoundBeanDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundBeanDefinitionException(Throwable cause) {
        super(cause);
    }

    public NotFoundBeanDefinitionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
