package com.pweb.springBackend.DTOs.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagDTO {
    private String name;
    private Long todoId;

    public TagDTO() {
    }

    public TagDTO(String name) {
        this.name = name;
    }
}
