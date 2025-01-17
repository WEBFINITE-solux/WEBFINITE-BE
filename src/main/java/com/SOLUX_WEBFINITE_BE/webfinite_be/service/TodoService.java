package com.SOLUX_WEBFINITE_BE.webfinite_be.service;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Todo;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.User;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.TodoRequestDto;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.TodoResponseDto;
import com.SOLUX_WEBFINITE_BE.webfinite_be.exception.TodoNotFoundException;
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
        List<Todo> todos = todoRepository.findByUser_Id(userId);
        if (todos.isEmpty()) {
            throw new TodoNotFoundException("Todo 목록에 항목이 없습니다");
        }
        return todos.stream()
                .map(TodoResponseDto::new)
                .collect(Collectors.toList());
    }

    // 날짜 범위와 userId 기반 조회
    public List<TodoResponseDto> getTodosByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Todo> todos = todoRepository.findTodosByUserIdAndDateRange(userId, startDate, endDate);
        if (todos.isEmpty()) {
            throw new TodoNotFoundException("조회한 날짜에 Todo항목이 없습니다");
        }
        return todos.stream()
                .map(TodoResponseDto::new)
                .collect(Collectors.toList());
    }

    // Todo 추가
    public TodoResponseDto addTodo(TodoRequestDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

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
                .orElseThrow(() -> new TodoNotFoundException("없는 Todo 항목입니다"));
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
                .orElseThrow(() -> new TodoNotFoundException("이미 지워진 항목입니다"));
        todoRepository.deleteTodo(todoId);
    }

    // Todo 조회
    public Todo getTodoById(Long todoId) {
        return todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
    }

    // Todo 완료 상태 업데이트
    @Transactional
    public void updateTodoCompletion(Long todoId, boolean isCompleted) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new TodoNotFoundException("Todo 항목이 없습니다"));
        todo.setIsCompleted(isCompleted);
        todoRepository.updateTodoCompletion(todoId, isCompleted);
    }
}

