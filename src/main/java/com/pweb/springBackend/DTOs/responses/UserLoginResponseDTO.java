package com.pweb.springBackend.DTOs.responses;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class UserLoginResponseDTO extends ResponseDTO {
    private String token;
    private String email;

    public UserLoginResponseDTO(String token, String email) {
        super("User logged in successfully!", HttpStatus.OK);
        this.token = token;
        this.email = email;
    }
}
