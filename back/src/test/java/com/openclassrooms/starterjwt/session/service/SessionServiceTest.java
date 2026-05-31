package com.openclassrooms.starterjwt.session.service;

import com.openclassrooms.starterjwt.session.exception.InvalidParticipationException;
import com.openclassrooms.starterjwt.session.exception.SessionNotFoundException;
import com.openclassrooms.starterjwt.session.model.Session;
import com.openclassrooms.starterjwt.session.repository.SessionRepository;
import com.openclassrooms.starterjwt.user.exception.UserNotFoundException;
import com.openclassrooms.starterjwt.user.model.User;
import com.openclassrooms.starterjwt.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionService - tests unitaires")
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    private User user(Long id) {
        return User.builder().id(id).email("u" + id + "@test.com")
                .firstName("F").lastName("L").password("p").admin(false).build();
    }

    private Session session(Long id, List<User> users) {
        return Session.builder().id(id).name("Yoga").description("desc").users(users).build();
    }

    @Test
    @DisplayName("create délègue au repository")
    void create_savesSession() {
        Session session = session(1L, new ArrayList<>());
        when(sessionRepository.save(session)).thenReturn(session);

        assertThat(sessionService.create(session)).isEqualTo(session);
        verify(sessionRepository).save(session);
    }

    @Test
    @DisplayName("delete délègue au repository")
    void delete_callsRepository() {
        sessionService.delete(1L);
        verify(sessionRepository).deleteById(1L);
    }

    @Test
    @DisplayName("findAll retourne toutes les sessions")
    void findAll_returnsSessions() {
        List<Session> sessions = List.of(session(1L, new ArrayList<>()), session(2L, new ArrayList<>()));
        when(sessionRepository.findAll()).thenReturn(sessions);

        assertThat(sessionService.findAll()).isEqualTo(sessions);
    }

    @Test
    @DisplayName("getById retourne la session existante")
    void getById_whenExists_returnsSession() {
        Session session = session(1L, new ArrayList<>());
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertThat(sessionService.getById(1L)).isEqualTo(session);
    }

    @Test
    @DisplayName("getById lève SessionNotFoundException si absente")
    void getById_whenMissing_throws() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.getById(1L))
                .isInstanceOf(SessionNotFoundException.class);
    }

    @Test
    @DisplayName("update fixe l'id et enregistre")
    void update_setsIdAndSaves() {
        Session session = session(null, new ArrayList<>());
        when(sessionRepository.save(any(Session.class))).thenAnswer(i -> i.getArgument(0));

        Session result = sessionService.update(5L, session);

        assertThat(result.getId()).isEqualTo(5L);
        verify(sessionRepository).save(session);
    }

    @Test
    @DisplayName("participate ajoute l'utilisateur à la session")
    void participate_whenNotYetParticipating_addsUser() {
        Session session = session(1L, new ArrayList<>());
        User user = user(2L);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        sessionService.participate(1L, 2L);

        assertThat(session.getUsers()).contains(user);
        verify(sessionRepository).save(session);
    }

    @Test
    @DisplayName("participate lève SessionNotFoundException si session absente")
    void participate_whenSessionMissing_throws() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.participate(1L, 2L))
                .isInstanceOf(SessionNotFoundException.class);
    }

    @Test
    @DisplayName("participate lève UserNotFoundException si utilisateur absent")
    void participate_whenUserMissing_throws() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session(1L, new ArrayList<>())));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.participate(1L, 2L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("participate lève InvalidParticipationException si déjà inscrit")
    void participate_whenAlreadyParticipating_throws() {
        User user = user(2L);
        Session session = session(1L, new ArrayList<>(List.of(user)));
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> sessionService.participate(1L, 2L))
                .isInstanceOf(InvalidParticipationException.class);
        verify(sessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("noLongerParticipate retire l'utilisateur de la session")
    void noLongerParticipate_whenParticipating_removesUser() {
        User user = user(2L);
        Session session = session(1L, new ArrayList<>(List.of(user)));
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        sessionService.noLongerParticipate(1L, 2L);

        assertThat(session.getUsers()).doesNotContain(user);
        verify(sessionRepository).save(session);
    }

    @Test
    @DisplayName("noLongerParticipate lève SessionNotFoundException si session absente")
    void noLongerParticipate_whenSessionMissing_throws() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.noLongerParticipate(1L, 2L))
                .isInstanceOf(SessionNotFoundException.class);
    }

    @Test
    @DisplayName("noLongerParticipate lève InvalidParticipationException si non inscrit")
    void noLongerParticipate_whenNotParticipating_throws() {
        Session session = session(1L, new ArrayList<>());
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> sessionService.noLongerParticipate(1L, 2L))
                .isInstanceOf(InvalidParticipationException.class);
        verify(sessionRepository, never()).save(any());
    }
}
