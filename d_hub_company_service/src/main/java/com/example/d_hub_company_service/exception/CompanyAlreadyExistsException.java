package com.example.d_hub_company_service.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CompanyAlreadyExistsException extends ResponseStatusException {

    public CompanyAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
