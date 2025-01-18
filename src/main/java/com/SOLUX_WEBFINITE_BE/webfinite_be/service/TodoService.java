package com.SOLUX_WEBFINITE_BE.webfinite_be.service;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Todo;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.User;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.TodoRequestDto;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.TodoResponseDto;
import com.SOLUX_WEBFINITE_BE.webfinite_be.exception.*;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.TodoRepository;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoService {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoService(TodoRepository todoRepository, UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    // 사용자 ID로 Todo 항목 조회
    public List<TodoResponseDto> getTodosByUserId(Long userId) {
        // 사용자 존재 여부를 확인하고 예외 처리
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException()); // USER_NOT_FOUND 예외 처리

        // 해당 사용자의 Todo 목록 조회
        List<Todo> todos = todoRepository.findByUser_Id(user.getId());

        // Todo 목록이 비어있다면 예외 처리
        if (todos.isEmpty()) {
            throw new TodoListEmptyException(); // TODO_LIST_EMPTY 예외 처리
        }

        return todos.stream()
                .map(TodoResponseDto::new)
                .collect(Collectors.toList());
    }

    // 날짜 범위와 userId 기반 조회
    public List<TodoResponseDto> getTodosByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Todo> todos = todoRepository.findTodosByUserIdAndDateRange(userId, startDate, endDate);
        if (todos.isEmpty()) {
            throw new TodoDateRangeEmptyException(); // TODO_DATE_RANGE_EMPTY 예외 처리
        }
        return todos.stream()
                .map(TodoResponseDto::new)
                .collect(Collectors.toList());
    }

    // Todo 추가
    public TodoResponseDto addTodo(TodoRequestDto request) {
        if (request.getTodoContent().isEmpty()) {
            throw new EmptyTodoContentException();  // T-001 예외 처리
        }
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException());  // U-002 예외 처리

        Todo todo = new Todo();
        todo.setTodoContent(request.getTodoContent());
        todo.setStartDate(request.getStartDate());
        todo.setEndDate(request.getEndDate());
        todo.setIsCompleted(request.isCompleted());
        todo.setUser(user);

        Todo savedTodo = todoRepository.save(todo);
        return new TodoResponseDto(savedTodo);
    }

    // Todo 수정
    public TodoResponseDto updateTodo(Long todoId, TodoRequestDto request) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new TodoNotFoundException());  // TODO_NOT_FOUND 예외 처리
        todo.setTodoContent(request.getTodoContent());
        todo.setStartDate(request.getStartDate());
        todo.setEndDate(request.getEndDate());

        Todo updatedTodo = todoRepository.save(todo);
        return new TodoResponseDto(updatedTodo);
    }

    // Todo 삭제
    @Transactional
    public void deleteTodo(Long todoId) {
        todoRepository.findById(todoId)
                .orElseThrow(() -> new AlreadyDeletedTodoException()); // T-005 예외 처리
        todoRepository.deleteTodo(todoId);
    }

    // Todo 조회
    public Todo getTodoById(Long todoId) {
        return todoRepository.findById(todoId)
                .orElseThrow(() -> new TodoNotFoundException());  // TODO_NOT_FOUND 예외 처리
    }

    // Todo 완료 상태 업데이트
    @Transactional
    public void updateTodoCompletion(Long todoId, boolean isCompleted) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new TodoNotFoundException());  // TODO_NOT_FOUND 예외 처리
        todo.setIsCompleted(isCompleted);
        todoRepository.updateTodoCompletion(todoId, isCompleted);
    }
}

