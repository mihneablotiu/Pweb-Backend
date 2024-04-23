package com.pweb.springBackend.DTOs.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoResponseDTO {
    private Long todoId;
    private String title;
    private String description;
    private Boolean isCompleted;

    public TodoResponseDTO() {
    }

    public TodoResponseDTO(Long todoId, String title, String description, Boolean isCompleted) {
        this.todoId = todoId;
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
    }
}
