package com.openclassrooms.starterjwt.auth.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthTokenFilter - tests unitaires")
class AuthTokenFilterTest {

    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    private AuthTokenFilter filter;

    @BeforeEach
    void setUp() {
        filter = new AuthTokenFilter();
        ReflectionTestUtils.setField(filter, "jwtUtils", jwtUtils);
        ReflectionTestUtils.setField(filter, "userDetailsService", userDetailsService);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("token valide : positionne l'authentification dans le contexte")
    void validToken_setsAuthentication() throws Exception {
        UserDetails userDetails = new User("user@test.com", "pwd", Collections.emptyList());
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
        when(jwtUtils.validateJwtToken("valid.jwt.token")).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("valid.jwt.token")).thenReturn("user@test.com");
        when(userDetailsService.loadUserByUsername("user@test.com")).thenReturn(userDetails);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("user@test.com");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("aucun header : ne positionne pas d'authentification")
    void noHeader_doesNotAuthenticate() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("token invalide : ne positionne pas d'authentification")
    void invalidToken_doesNotAuthenticate() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer bad.token");
        when(jwtUtils.validateJwtToken("bad.token")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("exception interne : la chaîne de filtres continue")
    void exception_doesNotBreakChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
        when(jwtUtils.validateJwtToken("valid.jwt.token")).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("valid.jwt.token")).thenReturn("user@test.com");
        when(userDetailsService.loadUserByUsername("user@test.com"))
                .thenThrow(new RuntimeException("boom"));

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
}
