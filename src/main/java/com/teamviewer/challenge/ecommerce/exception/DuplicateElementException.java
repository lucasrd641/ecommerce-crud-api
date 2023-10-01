package com.teamviewer.challenge.ecommerce.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DuplicateElementException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public DuplicateElementException(String message) {
        super(message);
    }
}