package com.openclassrooms.starterjwt.config;

import com.openclassrooms.starterjwt.session.exception.InvalidParticipationException;
import com.openclassrooms.starterjwt.session.exception.SessionNotFoundException;
import com.openclassrooms.starterjwt.user.exception.EmailAlreadyUsedException;
import com.openclassrooms.starterjwt.user.exception.UnauthorizedDeleteException;
import com.openclassrooms.starterjwt.user.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler - tests unitaires")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("onNotFound -> 404 avec le message de l'exception")
    void onNotFound_returns404() {
        ResponseEntity<Map<String, String>> response =
                handler.onNotFound(new SessionNotFoundException("12"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("message", "Session not found: 12");
    }

    @Test
    @DisplayName("onNotFound gère aussi UserNotFoundException")
    void onNotFound_handlesUserNotFound() {
        ResponseEntity<Map<String, String>> response =
                handler.onNotFound(new UserNotFoundException("5"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("onEmailInUse -> 400 avec message dédié")
    void onEmailInUse_returns400() {
        ResponseEntity<Map<String, String>> response =
                handler.onEmailInUse(new EmailAlreadyUsedException("a@b.com"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("message", "Error: Email is already taken!");
    }

    @Test
    @DisplayName("onInvalidParticipation -> 400 avec le message de l'exception")
    void onInvalidParticipation_returns400() {
        ResponseEntity<Map<String, String>> response =
                handler.onInvalidParticipation(new InvalidParticipationException("already"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("message", "already");
    }

    @Test
    @DisplayName("onUnauthorizedDelete -> 401")
    void onUnauthorizedDelete_returns401() {
        ResponseEntity<Map<String, String>> response =
                handler.onUnauthorizedDelete(new UnauthorizedDeleteException());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("onInvalidPathParam -> 400 avec message générique")
    void onInvalidPathParam_returns400() {
        ResponseEntity<Map<String, String>> response =
                handler.onInvalidPathParam(new NumberFormatException("nope"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("message", "Invalid path parameter");
    }
}
