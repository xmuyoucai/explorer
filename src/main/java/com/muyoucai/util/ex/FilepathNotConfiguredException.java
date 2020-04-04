package com.muyoucai.util.ex;

public class FilepathNotConfiguredException extends CustomException {

    public FilepathNotConfiguredException() {
    }

    public FilepathNotConfiguredException(String message) {
        super(message);
    }

    public FilepathNotConfiguredException(String message, Throwable cause) {
        super(message, cause);
    }

    public FilepathNotConfiguredException(Throwable cause) {
        super(cause);
    }

    public FilepathNotConfiguredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
