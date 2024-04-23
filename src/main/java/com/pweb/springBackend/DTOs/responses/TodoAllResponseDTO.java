package com.pweb.springBackend.DTOs.responses;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;

@Getter
@Setter
public class TodoAllResponseDTO extends ResponseDTO {
    private ArrayList<TodoResponseDTO> todos;

    public TodoAllResponseDTO(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public TodoAllResponseDTO(String message, HttpStatus httpStatus, ArrayList<TodoResponseDTO> todos) {
        super(message, httpStatus);
        this.todos = todos;
    }
}
