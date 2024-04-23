package com.pweb.springBackend.DTOs.responses;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class ResponseDTO {
    private String message;
    private HttpStatus httpStatus;

    public ResponseDTO(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
