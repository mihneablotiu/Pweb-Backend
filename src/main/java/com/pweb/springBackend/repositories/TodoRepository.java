package com.pweb.springBackend.repositories;

import com.pweb.springBackend.entities.Todo;
import com.pweb.springBackend.entities.TodoList;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    boolean existsTodoByTitleAndTodoList(String title, TodoList todoList);
    Optional<Todo> findByTitleAndTodoList(String title, TodoList todoList);
    Optional<Todo> findByTodoIdAndTodoList(Long todoId, TodoList todoList);
    Optional<ArrayList<Todo>> findAllByTodoList(TodoList todoList, PageRequest pageRequest);
    Optional<TodoList> findByTodoId(Long todoId);
}
