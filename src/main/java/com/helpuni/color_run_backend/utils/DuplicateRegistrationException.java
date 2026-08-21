package com.helpuni.color_run_backend.utils;

public class DuplicateRegistrationException extends RuntimeException{

    public DuplicateRegistrationException(String message){
        super(message);
    }
}
