package com.pweb.springBackend.DTOs.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRoleDTO {
    private String email;
    private String role;

    public UserUpdateRoleDTO(String email, String role) {
        this.email = email;
        this.role = role;
    }
}
