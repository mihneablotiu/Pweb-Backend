package com.pweb.springBackend.DTOs.responses;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class IndividualTodoListResponseDTO extends ResponseDTO {
    private Long todoListId;
    private String title;
    private String description;

    public IndividualTodoListResponseDTO(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public IndividualTodoListResponseDTO(String message, HttpStatus httpStatus, Long todoListId, String title, String description) {
        super(message, httpStatus);
        this.todoListId = todoListId;
        this.title = title;
        this.description = description;
    }
}
