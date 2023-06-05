package com.drdolgosheev.restwebfluxsecuredemo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class InvalidTokenException extends ApiException{
    public InvalidTokenException(String message) {
        super(message, "AC000");
    }
}
