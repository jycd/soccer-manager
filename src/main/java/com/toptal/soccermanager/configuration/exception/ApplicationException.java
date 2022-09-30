package com.toptal.soccermanager.configuration.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends Exception {
    private ApplicationError applicationError;
    private String additionalMessage;

    private static final long serialVersionUID = 1L;

    public ApplicationException(ApplicationError applicationError) {
        this.applicationError = applicationError;
    }

    public ApplicationException(ApplicationError applicationError, String message) {
        this.applicationError = applicationError;
        this.additionalMessage = message;
    }

    public ApplicationException(ApplicationError applicationError, String message, Exception ex) {
        super(ex);
        this.applicationError = applicationError;
        this.additionalMessage = message;
    }
}
