package com.example.d_hub_user_service.exception;

public class CompanyNotFoundException extends RuntimeException{

    public CompanyNotFoundException(String message) {
        super(message);
    }
}
