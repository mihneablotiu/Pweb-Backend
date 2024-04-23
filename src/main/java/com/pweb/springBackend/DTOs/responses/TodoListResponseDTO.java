package com.pweb.springBackend.DTOs.responses;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class TodoListResponseDTO {
    private Long todoListId;
    private String title;
    private String description;

    public TodoListResponseDTO() {

    }

    public TodoListResponseDTO(Long todoListId, String title, String description) {
        this.todoListId = todoListId;
        this.title = title;
        this.description = description;
    }
}
