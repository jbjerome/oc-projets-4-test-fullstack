package com.openclassrooms.starterjwt.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtUtils - tests unitaires")
class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        // Secret base64 décodant sur ≥ 512 bits, requis par HS512 (jjwt 0.12.x).
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret",
                "dW5pdC1hbmQtaW50ZWdyYXRpb24tdGVzdC1zaWduaW5nLWtleS0wMTIzNDU2Nzg5LWFiY2RlZmdoaWprbG1ub3A=");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3600000);
    }

    private String generateTokenFor(String username) {
        UserDetailsImpl principal = UserDetailsImpl.builder().id(1L).username(username).build();
        when(authentication.getPrincipal()).thenReturn(principal);
        return jwtUtils.generateJwtToken(authentication);
    }

    @Test
    @DisplayName("génère un token, en extrait le username et le valide")
    void generateAndValidateToken() {
        String token = generateTokenFor("user@test.com");

        assertThat(token).isNotBlank();
        assertThat(jwtUtils.getUserNameFromJwtToken(token)).isEqualTo("user@test.com");
        assertThat(jwtUtils.validateJwtToken(token)).isTrue();
    }

    @Test
    @DisplayName("validateJwtToken renvoie false pour un token malformé")
    void validate_malformedToken_returnsFalse() {
        assertThat(jwtUtils.validateJwtToken("not-a-real-jwt")).isFalse();
    }

    @Test
    @DisplayName("validateJwtToken renvoie false pour une chaîne vide")
    void validate_emptyToken_returnsFalse() {
        assertThat(jwtUtils.validateJwtToken("")).isFalse();
    }

    @Test
    @DisplayName("validateJwtToken renvoie false pour une signature invalide")
    void validate_badSignature_returnsFalse() {
        JwtUtils otherSecret = new JwtUtils();
        ReflectionTestUtils.setField(otherSecret, "jwtSecret",
                "YS1jb21wbGV0ZWx5LWRpZmZlcmVudC1zaWduaW5nLWtleS1mb3ItYmFkLXNpZ25hdHVyZS10ZXN0LTk4NzY1NDM=");
        ReflectionTestUtils.setField(otherSecret, "jwtExpirationMs", 3600000);
        String token = otherSecret.generateJwtToken(authentication());

        assertThat(jwtUtils.validateJwtToken(token)).isFalse();
    }

    @Test
    @DisplayName("validateJwtToken renvoie false pour un token non signé (unsupported)")
    void validate_unsignedToken_returnsFalse() {
        String unsigned = io.jsonwebtoken.Jwts.builder().setSubject("user@test.com").compact();

        assertThat(jwtUtils.validateJwtToken(unsigned)).isFalse();
    }

    @Test
    @DisplayName("validateJwtToken renvoie false pour un token expiré")
    void validate_expiredToken_returnsFalse() {
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", -1000);
        String expired = generateTokenFor("user@test.com");

        assertThat(jwtUtils.validateJwtToken(expired)).isFalse();
    }

    private Authentication authentication() {
        UserDetailsImpl principal = UserDetailsImpl.builder().id(1L).username("user@test.com").build();
        when(authentication.getPrincipal()).thenReturn(principal);
        return authentication;
    }
}
