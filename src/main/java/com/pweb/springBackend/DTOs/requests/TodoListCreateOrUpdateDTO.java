package com.pweb.springBackend.DTOs.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoListCreateOrUpdateDTO {
    private String title;
    private String description;

    public TodoListCreateOrUpdateDTO() {

    }

    public TodoListCreateOrUpdateDTO(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
