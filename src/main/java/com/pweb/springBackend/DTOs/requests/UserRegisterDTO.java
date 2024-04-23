package com.pweb.springBackend.DTOs.requests;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRegisterDTO {
    private String username;
    private String email;
    private String password;

    public UserRegisterDTO(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
