package com.pweb.springBackend.repositories;

import com.pweb.springBackend.entities.TodoList;
import com.pweb.springBackend.entities.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface TodoListRepository extends JpaRepository<TodoList, Long> {
    boolean existsTodoListByTitleAndUser(String title, User user);
    Optional<TodoList> findByTitleAndUser(String title, User user);
    Optional<ArrayList<TodoList>> findAllByUser(User user, PageRequest pageRequest);
    Optional<TodoList> findByTodoListIdAndUser(Long todoListId, User user);
}
