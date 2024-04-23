package com.pweb.springBackend.controllers;

import com.pweb.springBackend.DTOs.requests.TagDTO;
import com.pweb.springBackend.DTOs.responses.ErrorResponseDTO;
import com.pweb.springBackend.DTOs.responses.ResponseDTO;
import com.pweb.springBackend.DTOs.responses.ValidResponseDTO;
import com.pweb.springBackend.configs.JwtUtil;
import com.pweb.springBackend.entities.Tag;
import com.pweb.springBackend.entities.Todo;
import com.pweb.springBackend.entities.TodoList;
import com.pweb.springBackend.entities.User;
import com.pweb.springBackend.repositories.TagRepository;
import com.pweb.springBackend.repositories.TodoRepository;
import com.pweb.springBackend.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Objects;

@RestController
@RequestMapping("/tags")
public class TagController {
    private final TagRepository tagRepository;
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public TagController(TagRepository tagRepository, TodoRepository todoRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.tagRepository = tagRepository;
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO> createTag(@RequestBody TagDTO tagDTO, @RequestHeader("Authorization") String token) {
        String TOKEN_PREFIX = "Bearer ";
        Claims claims = jwtUtil.parseJwtClaims(token.substring(TOKEN_PREFIX.length()));

        User user = userRepository.findByEmail(jwtUtil.getEmail(claims)).orElse(null);
        if (user == null) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("User not found", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
        }

        Todo todo = todoRepository.findById(tagDTO.getTodoId()).orElse(null);
        if (todo == null) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("Todo not found", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
        }

        TodoList todoList = todo.getTodoList();
        if (todoList == null) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("Todo list not found", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
        }

        User todoListUser = todoList.getUser();
        if (!todoListUser.getUserId().equals(user.getUserId())) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("Unauthorized", HttpStatus.UNAUTHORIZED);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponseDTO);
        }

        if (tagRepository.existsTagByName(tagDTO.getName())) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("Tag already exists", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
        }

        Tag tag = new Tag();
        tag.setName(tagDTO.getName());
        tag.setTodos(new ArrayList<>());
        tag.getTodos().add(todo);
        tagRepository.save(tag);

        if (todo.getTags() == null) {
            todo.setTags(new ArrayList<>());
        }

        todo.getTags().add(tag);
        todoRepository.save(todo);

        ValidResponseDTO validResponseDTO = new ValidResponseDTO("Tag created successfully", HttpStatus.CREATED);
        return ResponseEntity.status(HttpStatus.CREATED).body(validResponseDTO);
    }

    @PatchMapping("/update")
    public ResponseEntity<ResponseDTO> updateTag(@RequestBody TagDTO tagDTO, @RequestHeader("Authorization") String token) {
        String TOKEN_PREFIX = "Bearer ";
        Claims claims = jwtUtil.parseJwtClaims(token.substring(TOKEN_PREFIX.length()));

        User user = userRepository.findByEmail(jwtUtil.getEmail(claims)).orElse(null);
        if (user == null) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("User not found", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
        }

        Todo todo = todoRepository.findById(tagDTO.getTodoId()).orElse(null);
        if (todo == null) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("Todo not found", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
        }

        TodoList todoList = todo.getTodoList();
        if (todoList == null) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("Todo list not found", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
        }

        User todoListUser = todoList.getUser();
        if (!todoListUser.getUserId().equals(user.getUserId())) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("Unauthorized", HttpStatus.UNAUTHORIZED);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponseDTO);
        }

        if (!tagRepository.existsTagByName(tagDTO.getName())) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("Tag not found", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
        }

        Tag tag = tagRepository.findTagByName(tagDTO.getName()).orElse(null);
        if (todo.getTags() == null) {
            todo.setTags(new ArrayList<>());
        }

        if (tag.getTodos().contains(todo) && todo.getTags().contains(tag)) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("Tag already exists in todo", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
        }

        tag.getTodos().add(todo);
        todo.getTags().add(tag);

        tagRepository.save(tag);
        todoRepository.save(todo);

        ValidResponseDTO validResponseDTO = new ValidResponseDTO("Tag updated successfully", HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(validResponseDTO);
    }

    @DeleteMapping("/delete/{tagId}")
    public ResponseEntity<ResponseDTO> deleteTag(@PathVariable Long tagId, @RequestHeader("Authorization") String token) {
        Tag tag = tagRepository.findById(tagId).orElse(null);
        if (tag == null) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("Tag not found", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
        }

        tag.getTodos().forEach(todo -> todo.getTags().remove(tag));
        tag.getTodos().clear();

        tagRepository.save(tag);
        tagRepository.delete(tag);

        ValidResponseDTO validResponseDTO = new ValidResponseDTO("Tag deleted successfully", HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(validResponseDTO);
    }
}
