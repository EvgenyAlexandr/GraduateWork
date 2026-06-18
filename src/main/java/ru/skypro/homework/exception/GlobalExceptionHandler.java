package ru.skypro.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Глобальная обработка исключений безопасности, валидации и «ресурс не найден».
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Неверный логин или пароль при Basic Auth.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentials(BadCredentialsException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Попытка изменить или удалить чужой ресурс без роли ADMIN.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Void> handleAccessDenied(AccessDeniedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Объявление или комментарий с указанным id не существует.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Void> handleResourceNotFound(ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Невалидные поля DTO ({@code @Valid} в контроллерах).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Void> handleValidation(MethodArgumentNotValidException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Некорректные бизнес-аргументы: неверный текущий пароль, пустой файл изображения.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Ошибка сохранения, чтения или удаления файла изображения на диске.
     */
    @ExceptionHandler(ImageStorageException.class)
    public ResponseEntity<Void> handleImageStorage(ImageStorageException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
