package com.pweb.springBackend.DTOs.responses;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;

@Getter
@Setter
public class TodoListAllResponseDTO extends ResponseDTO {
    private ArrayList<TodoListResponseDTO> todoLists;

    public TodoListAllResponseDTO(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public TodoListAllResponseDTO(String message, HttpStatus httpStatus, ArrayList<TodoListResponseDTO> todoLists) {
        super(message, httpStatus);
        this.todoLists = todoLists;
    }
}
