package com.openclassrooms.starterjwt.config;

import com.openclassrooms.starterjwt.session.exception.InvalidParticipationException;
import com.openclassrooms.starterjwt.session.exception.SessionNotFoundException;
import com.openclassrooms.starterjwt.teacher.exception.TeacherNotFoundException;
import com.openclassrooms.starterjwt.user.exception.EmailAlreadyUsedException;
import com.openclassrooms.starterjwt.user.exception.UnauthorizedDeleteException;
import com.openclassrooms.starterjwt.user.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({UserNotFoundException.class, TeacherNotFoundException.class, SessionNotFoundException.class})
    public ResponseEntity<Map<String, String>> onNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<Map<String, String>> onEmailInUse(EmailAlreadyUsedException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", "Error: Email is already taken!"));
    }

    @ExceptionHandler(InvalidParticipationException.class)
    public ResponseEntity<Map<String, String>> onInvalidParticipation(InvalidParticipationException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedDeleteException.class)
    public ResponseEntity<Map<String, String>> onUnauthorizedDelete(UnauthorizedDeleteException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler({NumberFormatException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, String>> onInvalidPathParam(Exception ex) {
        return ResponseEntity.badRequest().body(Map.of("message", "Invalid path parameter"));
    }
}
