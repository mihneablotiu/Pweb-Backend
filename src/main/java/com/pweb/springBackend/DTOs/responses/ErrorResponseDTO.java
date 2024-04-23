package com.pweb.springBackend.DTOs.responses;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class ErrorResponseDTO extends ResponseDTO {
    public ErrorResponseDTO(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
