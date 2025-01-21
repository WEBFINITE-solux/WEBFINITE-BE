package com.SOLUX_WEBFINITE_BE.webfinite_be.controller;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Todo;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.TodoRequestDto;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.TodoResponseDto;
import com.SOLUX_WEBFINITE_BE.webfinite_be.exception.*;
import com.SOLUX_WEBFINITE_BE.webfinite_be.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    // 사용자 ID로 Todo 항목 조회
    @GetMapping("/user/{user_id}")
    public ResponseEntity<List<TodoResponseDto>> getTodosByUserId(
            @PathVariable("user_id") Long userId) {
        List<TodoResponseDto> todoResponseDtos = todoService.getTodosByUserId(userId);
        return ResponseEntity.ok(todoResponseDtos);
    }

    // Todo 추가
    @PostMapping
    public ResponseEntity<TodoResponseDto> addTodo(@RequestBody TodoRequestDto request) {
        // null 체크 추가
        if (request == null || request.getTodoContent() == null) {
            throw new EmptyTodoContentException(); // 예외 발생
        }

        // 추가된 Todo 객체를 응답 DTO로 반환
        TodoResponseDto response = todoService.addTodo(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{todo_id}")
    public ResponseEntity<TodoResponseDto> updateTodo(
            @PathVariable("todo_id") Long todoId,
            @RequestBody TodoRequestDto request) {
        if (todoId == null) {
            throw new IllegalArgumentException("할 일 ID가 비어 있습니다.");
        }

        TodoResponseDto response = todoService.updateTodo(todoId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{todo_id}")
    public ResponseEntity<String> deleteTodo(@PathVariable("todo_id") Long todoId) {
        if (todoId == null) {
            throw new IllegalArgumentException("할 일 ID가 비어 있습니다.");
        }

        todoService.deleteTodo(todoId);
        return ResponseEntity.ok("할 일이 성공적으로 삭제되었습니다.");
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<TodoResponseDto>> getTodosByUserIdAndDateRange(
            @RequestParam("userId") Long userId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<TodoResponseDto> response = todoService.getTodosByUserIdAndDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{todo_id}/complete")
    public ResponseEntity<TodoResponseDto> updateTodoCompletion(
            @PathVariable("todo_id") Long todoId,
            @RequestParam("isCompleted") boolean isCompleted) {

        todoService.updateTodoCompletion(todoId, isCompleted);
        Todo updatedTodo = todoService.getTodoById(todoId);  // 업데이트된 Todo 객체 조회
        return ResponseEntity.ok(new TodoResponseDto(updatedTodo));
    }

    @ExceptionHandler(EmptyTodoContentException.class)
    public ResponseEntity<String> handleTodoContentEmptyException(EmptyTodoContentException ex) {
        return ResponseEntity.badRequest().body(ex.getErrorCode().getMessage());
    }

    @ExceptionHandler(TodoNotFoundException.class)
    public ResponseEntity<String> handleTodoNotFoundException(TodoNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getErrorCode().getMessage());
    }

    @ExceptionHandler(AlreadyDeletedTodoException.class)
    public ResponseEntity<String> handleAlreadyDeletedTodoException(AlreadyDeletedTodoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getErrorCode().getMessage());
    }

    @ExceptionHandler(TodoDateRangeEmptyException.class)
    public ResponseEntity<String> handleTodoDateRangeEmptyException(TodoDateRangeEmptyException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getErrorCode().getMessage());
    }

    @ExceptionHandler(TodoListEmptyException.class)
    public ResponseEntity<String> handleTodoListEmptyException(TodoListEmptyException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getErrorCode().getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getErrorCode().getMessage());
    }
}