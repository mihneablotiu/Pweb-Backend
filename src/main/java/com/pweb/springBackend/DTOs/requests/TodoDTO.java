package com.pweb.springBackend.DTOs.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoDTO {
    private String title;
    private String description;
    private Boolean isCompleted;

    public TodoDTO() {
    }

    public TodoDTO(String title, String description, Boolean isCompleted) {
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
    }
}
