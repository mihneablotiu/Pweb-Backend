package com.pweb.springBackend.controllers;

import com.pweb.springBackend.DTOs.requests.TodoListDTO;
import com.pweb.springBackend.DTOs.responses.*;
import com.pweb.springBackend.configs.JwtUtil;
import com.pweb.springBackend.entities.TodoList;
import com.pweb.springBackend.entities.User;
import com.pweb.springBackend.repositories.TodoListRepository;
import com.pweb.springBackend.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/todolists")
public class TodoListController {
    private final TodoListRepository todoListRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public TodoListController(TodoListRepository todoListRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.todoListRepository = todoListRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO> createTodoList(@RequestBody TodoListDTO todoListDTO, @RequestHeader("Authorization") String token) {
        String TOKEN_PREFIX = "Bearer ";
        Claims claims = jwtUtil.parseJwtClaims(token.substring(TOKEN_PREFIX.length()));

        User user = userRepository.findByEmail(jwtUtil.getEmail(claims)).orElse(null);
        if (todoListRepository.existsTodoListByTitleAndUser(todoListDTO.getTitle(), user)) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO("Todo list already exists!", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        TodoList todoList = new TodoList();
        todoList.setTitle(todoListDTO.getTitle());
        todoList.setDescription(todoListDTO.getDescription());
        todoList.setUser(user);

        todoListRepository.save(todoList);

        ValidResponseDTO validResponse = new ValidResponseDTO("Todo list created successfully!", HttpStatus.CREATED);
        return ResponseEntity.status(HttpStatus.CREATED).body(validResponse);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDTO> updateTodoList(@RequestBody TodoListDTO todoListDTO, @RequestHeader("Authorization") String token) {
        String TOKEN_PREFIX = "Bearer ";
        Claims claims = jwtUtil.parseJwtClaims(token.substring(TOKEN_PREFIX.length()));

        User user = userRepository.findByEmail(jwtUtil.getEmail(claims)).orElse(null);
        TodoList todoList = todoListRepository.findByTitleAndUser(todoListDTO.getTitle(), user).orElse(null);

        if (todoList == null) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO("Todo list not found!", HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        todoList.setTitle(todoListDTO.getTitle());
        todoList.setDescription(todoListDTO.getDescription());

        todoListRepository.save(todoList);

        ValidResponseDTO validResponse = new ValidResponseDTO("Todo list updated successfully!", HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(validResponse);
    }

    @GetMapping("/get")
    public ResponseEntity<ResponseDTO> getTodoLists(@RequestHeader("Authorization") String token) {
        String TOKEN_PREFIX = "Bearer ";
        Claims claims = jwtUtil.parseJwtClaims(token.substring(TOKEN_PREFIX.length()));

        User user = userRepository.findByEmail(jwtUtil.getEmail(claims)).orElse(null);
        ArrayList<TodoList> todoLists = todoListRepository.findAllByUser(user, PageRequest.of(0, Integer.MAX_VALUE)).orElse(null);

        if (todoLists == null) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO("No todo lists found!", HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        TodoListAllResponseDTO todoListAllResponseDTO = new TodoListAllResponseDTO("Todo lists found!", HttpStatus.OK,
                todoLists.stream().map(t -> new TodoListResponseDTO(t.getTodoListId(), t.getTitle(), t.getDescription())).collect(Collectors.toCollection(ArrayList::new)));

        return ResponseEntity.status(HttpStatus.OK).body(todoListAllResponseDTO);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseDTO> getTodoList(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String TOKEN_PREFIX = "Bearer ";
        Claims claims = jwtUtil.parseJwtClaims(token.substring(TOKEN_PREFIX.length()));

        User user = userRepository.findByEmail(jwtUtil.getEmail(claims)).orElse(null);
        TodoList todoList = todoListRepository.findByTodoListIdAndUser(id, user).orElse(null);

        if (todoList == null) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO("Todo list not found!", HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        IndividualTodoListResponseDTO individualTodoListResponseDTO = new IndividualTodoListResponseDTO("Todo list found!", HttpStatus.OK);
        individualTodoListResponseDTO.setTodoListId(todoList.getTodoListId());
        individualTodoListResponseDTO.setTitle(todoList.getTitle());
        individualTodoListResponseDTO.setDescription(todoList.getDescription());

        return ResponseEntity.status(HttpStatus.OK).body(individualTodoListResponseDTO);
    }

    // Implement a get method with pagination
    @GetMapping("/get/{page}/{size}")
    public ResponseEntity<ResponseDTO> getTodoList(@PathVariable int page, @PathVariable int size, @RequestHeader("Authorization") String token) {
        String TOKEN_PREFIX = "Bearer ";
        Claims claims = jwtUtil.parseJwtClaims(token.substring(TOKEN_PREFIX.length()));

        User user = userRepository.findByEmail(jwtUtil.getEmail(claims)).orElse(null);

        PageRequest pageRequest = PageRequest.of(page, size);
        ArrayList<TodoList> todoLists = todoListRepository.findAllByUser(user, pageRequest).orElse(null);

        if (todoLists == null) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO("No todo lists found!", HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        TodoListAllResponseDTO todoListAllResponseDTO = new TodoListAllResponseDTO("Todo lists found!", HttpStatus.OK,
                todoLists.stream().map(t -> new TodoListResponseDTO(t.getTodoListId(), t.getTitle(), t.getDescription())).collect(Collectors.toCollection(ArrayList::new)));

        return ResponseEntity.status(HttpStatus.OK).body(todoListAllResponseDTO);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDTO> deleteTodoList(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String TOKEN_PREFIX = "Bearer ";
        Claims claims = jwtUtil.parseJwtClaims(token.substring(TOKEN_PREFIX.length()));

        User user = userRepository.findByEmail(jwtUtil.getEmail(claims)).orElse(null);
        TodoList todoList = todoListRepository.findByTodoListIdAndUser(id, user).orElse(null);

        if (todoList == null) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO("Todo list not found!", HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        todoListRepository.delete(todoList);

        ValidResponseDTO validResponse = new ValidResponseDTO("Todo list deleted successfully!", HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(validResponse);
    }
}
