package com.pweb.springBackend.DTOs.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoCreateOrUpdateDTO {
    private String title;
    private String description;
    private Boolean isCompleted;

    public TodoCreateOrUpdateDTO() {
    }

    public TodoCreateOrUpdateDTO(String title, String description, Boolean isCompleted) {
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
    }
}
