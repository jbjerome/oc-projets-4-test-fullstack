package com.openclassrooms.starterjwt.user.controller;

import com.openclassrooms.starterjwt.user.dto.UserDto;
import com.openclassrooms.starterjwt.user.mapper.UserMapper;
import com.openclassrooms.starterjwt.user.model.User;
import com.openclassrooms.starterjwt.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController - tests unitaires")
class UserControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("findById renvoie l'utilisateur mappé en DTO")
    void findById_returnsDto() {
        User user = User.builder().id(1L).email("user@test.com").firstName("John")
                .lastName("Doe").password("pwd").admin(false).build();
        UserDto dto = new UserDto();
        dto.setId(1L);
        when(userService.findById(1L)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(dto);

        ResponseEntity<?> response = userController.findById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dto);
    }

    @Test
    @DisplayName("delete supprime l'utilisateur courant (issu du contexte de sécurité)")
    void delete_usesAuthenticatedUsername() {
        UserDetails principal = new org.springframework.security.core.userdetails.User(
                "user@test.com", "pwd", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        ResponseEntity<?> response = userController.delete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userService).delete(1L, "user@test.com");
    }
}
