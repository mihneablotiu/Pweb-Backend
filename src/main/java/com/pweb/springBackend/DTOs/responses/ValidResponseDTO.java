package com.pweb.springBackend.DTOs.responses;

import org.springframework.http.HttpStatus;

public class ValidResponseDTO extends ResponseDTO {
    public ValidResponseDTO(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
