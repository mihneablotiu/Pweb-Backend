package com.pweb.springBackend.controllers;

import com.pweb.springBackend.DTOs.requests.TodoCreateOrUpdateDTO;
import com.pweb.springBackend.DTOs.responses.*;
import com.pweb.springBackend.configs.JwtUtil;
import com.pweb.springBackend.entities.Todo;
import com.pweb.springBackend.entities.TodoList;
import com.pweb.springBackend.entities.User;
import com.pweb.springBackend.repositories.TodoListRepository;
import com.pweb.springBackend.repositories.TodoRepository;
import com.pweb.springBackend.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/todos")
public class TodoController {
    private final TodoRepository todoRepository;
    private final TodoListRepository todoListRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public TodoController(TodoRepository todoRepository, TodoListRepository todoListRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.todoRepository = todoRepository;
        this.todoListRepository = todoListRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/create/{todoListId}")
    public ResponseEntity<ResponseDTO> createTodo(@RequestBody TodoCreateOrUpdateDTO todoCreateOrUpdateDTO,
                                                  @RequestHeader("Authorization") String token,
                                                  @PathVariable Long todoListId) {
        String TOKEN_PREFIX = "Bearer ";
        Claims claims = jwtUtil.parseJwtClaims(token.substring(TOKEN_PREFIX.length()));

        User user = userRepository.findByEmail(jwtUtil.getEmail(claims)).orElse(null);
        TodoList todoList = todoListRepository.findByTodoListIdAndUser(todoListId, user).orElse(null);

        if (todoList == null) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("Todo list id not found", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
        }

        if (todoRepository.existsTodoByTitleAndTodoList(todoCreateOrUpdateDTO.getTitle(), todoList)) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("Todo already exists!", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
        }

        Todo todo = new Todo();
        todo.setTitle(todoCreateOrUpdateDTO.getTitle());
        todo.setDescription(todoCreateOrUpdateDTO.getDescription());
        todo.setIsCompleted(todoCreateOrUpdateDTO.getIsCompleted());
        todo.setTodoList(todoList);

        todoRepository.save(todo);

        ValidResponseDTO validResponseDTO = new ValidResponseDTO("Todo created successfully!", HttpStatus.CREATED);
        return ResponseEntity.status(HttpStatus.CREATED).body(validResponseDTO);
    }

    @PutMapping("/update/{todoListId}")
    public ResponseEntity<ResponseDTO> updateTodo(@RequestBody TodoCreateOrUpdateDTO todoCreateOrUpdateDTO,
                                                  @RequestHeader("Authorization") String token,
                                                  @PathVariable Long todoListId) {
        String TOKEN_PREFIX = "Bearer ";
        Claims claims = jwtUtil.parseJwtClaims(token.substring(TOKEN_PREFIX.length()));

        User user = userRepository.findByEmail(jwtUtil.getEmail(claims)).orElse(null);
        TodoList todoList = todoListRepository.findByTodoListIdAndUser(todoListId, user).orElse(null);

        if (todoList == null) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("Todo list id not found", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
        }

        Todo todo = todoRepository.findByTitleAndTodoList(todoCreateOrUpdateDTO.getTitle(), todoList).orElse(null);

        if (todo == null) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("Todo not found!", HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDTO);
        }

        todo.setTitle(todoCreateOrUpdateDTO.getTitle());
        todo.setDescription(todoCreateOrUpdateDTO.getDescription());
        todo.setIsCompleted(todoCreateOrUpdateDTO.getIsCompleted());

        todoRepository.save(todo);

        ValidResponseDTO validResponseDTO = new ValidResponseDTO("Todo updated successfully!", HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(validResponseDTO);
    }

    @PatchMapping("/toggle/{todoListId}/{todoId}")
    public ResponseEntity<ResponseDTO> toggleTodo(@PathVariable Long todoListId,
                                                  @PathVariable Long todoId,
                                                  @RequestHeader("Authorization") String token) {
        String TOKEN_PREFIX = "Bearer ";
        Claims claims = jwtUtil.parseJwtClaims(token.substring(TOKEN_PREFIX.length()));

        User user = userRepository.findByEmail(jwtUtil.getEmail(claims)).orElse(null);
        TodoList todoList = todoListRepository.findByTodoListIdAndUser(todoListId, user).orElse(null);

        if (todoList == null) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("Todo list id not found", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
        }

        Todo todo = todoRepository.findByTodoIdAndTodoList(todoId, todoList).orElse(null);

        if (todo == null) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("Todo not found!", HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDTO);
        }

        todo.setIsCompleted(!todo.getIsCompleted());

        todoRepository.save(todo);

        ValidResponseDTO validResponseDTO = new ValidResponseDTO("Todo toggled successfully!", HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(validResponseDTO);
    }

    @DeleteMapping("/delete/{todoListId}/{todoId}")
    public ResponseEntity<ResponseDTO> deleteTodo(@PathVariable Long todoListId,
                                                  @PathVariable Long todoId,
                                                  @RequestHeader("Authorization") String token) {
        String TOKEN_PREFIX = "Bearer ";
        Claims claims = jwtUtil.parseJwtClaims(token.substring(TOKEN_PREFIX.length()));

        User user = userRepository.findByEmail(jwtUtil.getEmail(claims)).orElse(null);
        TodoList todoList = todoListRepository.findByTodoListIdAndUser(todoListId, user).orElse(null);

        if (todoList == null) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("Todo list id not found", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
        }

        Todo todo = todoRepository.findById(todoId).orElse(null);

        if (todo == null) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("Todo not found!", HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDTO);
        }

        todoRepository.delete(todo);

        ValidResponseDTO validResponseDTO = new ValidResponseDTO("Todo deleted successfully!", HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(validResponseDTO);
    }

    @GetMapping("/get/{todoListId}")
    public ResponseEntity<ResponseDTO> getTodos(@PathVariable Long todoListId, @RequestHeader("Authorization") String token) {
        String TOKEN_PREFIX = "Bearer ";
        Claims claims = jwtUtil.parseJwtClaims(token.substring(TOKEN_PREFIX.length()));

        User user = userRepository.findByEmail(jwtUtil.getEmail(claims)).orElse(null);
        TodoList todoList = todoListRepository.findByTodoListIdAndUser(todoListId, user).orElse(null);

        if (todoList == null) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("Todo list id not found", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
        }

        ArrayList<Todo> todos = todoRepository.findAllByTodoList(todoList, PageRequest.of(0, Integer.MAX_VALUE)).orElse(null);

        if (todos == null) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("No todos found!", HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDTO);
        }

        TodoAllResponseDTO todoAllResponseDTO = new TodoAllResponseDTO("Todos found!", HttpStatus.OK,
                todos.stream().map(t -> new TodoResponseDTO(t.getTodoId(), t.getTitle(), t.getDescription(), t.getIsCompleted())).collect(Collectors.toCollection(ArrayList::new)));

        return ResponseEntity.status(HttpStatus.OK).body(todoAllResponseDTO);
    }

    @GetMapping("/get/{todoListId}/{page}/{size}")
    public ResponseEntity<ResponseDTO> getTodos(@PathVariable Long todoListId,
                                                @PathVariable int page,
                                                @PathVariable int size,
                                                @RequestHeader("Authorization") String token) {
        String TOKEN_PREFIX = "Bearer ";
        Claims claims = jwtUtil.parseJwtClaims(token.substring(TOKEN_PREFIX.length()));

        User user = userRepository.findByEmail(jwtUtil.getEmail(claims)).orElse(null);
        TodoList todoList = todoListRepository.findByTodoListIdAndUser(todoListId, user).orElse(null);

        if (todoList == null) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("Todo list id not found", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
        }

        ArrayList<Todo> todos = todoRepository.findAllByTodoList(todoList, PageRequest.of(page, size)).orElse(null);

        if (todos == null) {
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO("No todos found!", HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDTO);
        }

        TodoAllResponseDTO todoAllResponseDTO = new TodoAllResponseDTO("Todos found!", HttpStatus.OK,
                todos.stream().map(t -> new TodoResponseDTO(t.getTodoId(), t.getTitle(), t.getDescription(), t.getIsCompleted())).collect(Collectors.toCollection(ArrayList::new)));

        return ResponseEntity.status(HttpStatus.OK).body(todoAllResponseDTO);
    }
}
