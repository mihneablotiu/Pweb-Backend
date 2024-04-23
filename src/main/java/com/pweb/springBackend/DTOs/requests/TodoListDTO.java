package com.pweb.springBackend.DTOs.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoListDTO {
    private String title;
    private String description;

    public TodoListDTO() {

    }

    public TodoListDTO(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
