package com.muyoucai.util.ex;

public class MissingGitFileAnnotationException extends CustomException {

    public MissingGitFileAnnotationException() {
    }

    public MissingGitFileAnnotationException(String message) {
        super(message);
    }

    public MissingGitFileAnnotationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingGitFileAnnotationException(Throwable cause) {
        super(cause);
    }

    public MissingGitFileAnnotationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
