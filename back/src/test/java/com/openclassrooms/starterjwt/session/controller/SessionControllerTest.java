package com.openclassrooms.starterjwt.session.controller;

import com.openclassrooms.starterjwt.session.dto.SessionDto;
import com.openclassrooms.starterjwt.session.mapper.SessionMapper;
import com.openclassrooms.starterjwt.session.model.Session;
import com.openclassrooms.starterjwt.session.service.SessionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionController - tests unitaires")
class SessionControllerTest {

    @Mock
    private SessionService sessionService;
    @Mock
    private SessionMapper sessionMapper;

    @InjectMocks
    private SessionController sessionController;

    @Test
    @DisplayName("findById renvoie la session mappée")
    void findById_returnsDto() {
        Session session = Session.builder().id(1L).name("Yoga").build();
        SessionDto dto = new SessionDto();
        when(sessionService.getById(1L)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(dto);

        ResponseEntity<?> response = sessionController.findById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dto);
    }

    @Test
    @DisplayName("findAll renvoie la liste mappée")
    void findAll_returnsDtoList() {
        List<Session> sessions = List.of(Session.builder().id(1L).build());
        List<SessionDto> dtos = List.of(new SessionDto());
        when(sessionService.findAll()).thenReturn(sessions);
        when(sessionMapper.toDto(sessions)).thenReturn(dtos);

        ResponseEntity<?> response = sessionController.findAll();

        assertThat(response.getBody()).isEqualTo(dtos);
    }

    @Test
    @DisplayName("create mappe, crée et renvoie la session")
    void create_returnsCreatedDto() {
        SessionDto dto = new SessionDto();
        Session entity = Session.builder().id(1L).build();
        when(sessionMapper.toEntity(dto)).thenReturn(entity);
        when(sessionService.create(entity)).thenReturn(entity);
        when(sessionMapper.toDto(entity)).thenReturn(dto);

        ResponseEntity<?> response = sessionController.create(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dto);
    }

    @Test
    @DisplayName("update mappe, met à jour et renvoie la session")
    void update_returnsUpdatedDto() {
        SessionDto dto = new SessionDto();
        Session entity = Session.builder().id(1L).build();
        when(sessionMapper.toEntity(dto)).thenReturn(entity);
        when(sessionService.update(1L, entity)).thenReturn(entity);
        when(sessionMapper.toDto(entity)).thenReturn(dto);

        ResponseEntity<?> response = sessionController.update(1L, dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dto);
    }

    @Test
    @DisplayName("delete vérifie l'existence puis supprime")
    void delete_callsService() {
        Session session = Session.builder().id(1L).build();
        when(sessionService.getById(1L)).thenReturn(session);

        ResponseEntity<?> response = sessionController.delete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(sessionService).getById(1L);
        verify(sessionService).delete(1L);
    }

    @Test
    @DisplayName("participate délègue au service")
    void participate_callsService() {
        ResponseEntity<?> response = sessionController.participate(1L, 2L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(sessionService).participate(1L, 2L);
    }

    @Test
    @DisplayName("noLongerParticipate délègue au service")
    void noLongerParticipate_callsService() {
        ResponseEntity<?> response = sessionController.noLongerParticipate(1L, 2L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(sessionService).noLongerParticipate(1L, 2L);
    }
}
