package com.crediya.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtReactiveAuthenticationManager Tests")
class JwtReactiveAuthenticationManagerTest {

    @Mock
    private JwtProperties jwtProperties;
    
    private JwtReactiveAuthenticationManager authenticationManager;
    private SecretKey secretKey;
    private String validSecret = "QnE1T2lXbVRhV3RzR2VOUXlHaFZ2d2dyU2p2a1R2TnM=";
    private String validIssuer = "autenticacion-service";

    @BeforeEach
    void setUp() {
        when(jwtProperties.getSecret()).thenReturn(validSecret);
        when(jwtProperties.getIssuer()).thenReturn(validIssuer);
        
        secretKey = Keys.hmacShaKeyFor(validSecret.getBytes(StandardCharsets.UTF_8));
        authenticationManager = new JwtReactiveAuthenticationManager(jwtProperties);
    }

    @Test
    @DisplayName("Debe autenticar correctamente con token JWT válido")
    void shouldAuthenticateSuccessfullyWithValidJwtToken() {
        // Given
        String userId = "user123";
        List<String> roles = List.of("ADMIN", "USER");
        String token = createValidToken(userId, roles);
        
        Authentication auth = new UsernamePasswordAuthenticationToken(null, token, null);

        // When
        Mono<Authentication> result = authenticationManager.authenticate(auth);

        // Then
        StepVerifier.create(result)
                .assertNext(authentication -> {
                    assertEquals(userId, authentication.getPrincipal());
                    assertEquals(token, authentication.getCredentials());
                    assertEquals(2, authentication.getAuthorities().size());
                    assertTrue(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
                    assertTrue(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
                })
                .verifyComplete();
    }


    @Test
    @DisplayName("Debe retornar Mono vacío con issuer incorrecto")
    void shouldReturnEmptyMonoWithIncorrectIssuer() {
        // Given
        String userId = "user123";
        String token = createTokenWithIssuer(userId, "wrong-issuer");
        Authentication auth = new UsernamePasswordAuthenticationToken(null, token, null);

        // When
        Mono<Authentication> result = authenticationManager.authenticate(auth);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar Mono vacío sin issuer cuando se espera uno")
    void shouldReturnEmptyMonoWithoutIssuerWhenExpected() {
        // Given
        String userId = "user123";
        String token = createTokenWithoutIssuer(userId);
        Authentication auth = new UsernamePasswordAuthenticationToken(null, token, null);

        // When
        Mono<Authentication> result = authenticationManager.authenticate(auth);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe autenticar correctamente sin validación de issuer")
    void shouldAuthenticateSuccessfullyWithoutIssuerValidation() {
        // Given
        when(jwtProperties.getIssuer()).thenReturn(null);
        authenticationManager = new JwtReactiveAuthenticationManager(jwtProperties);
        
        String userId = "user123";
        String token = createTokenWithoutIssuer(userId);
        Authentication auth = new UsernamePasswordAuthenticationToken(null, token, null);

        // When
        Mono<Authentication> result = authenticationManager.authenticate(auth);

        // Then
        StepVerifier.create(result)
                .assertNext(authentication -> {
                    assertEquals(userId, authentication.getPrincipal());
                    assertEquals(token, authentication.getCredentials());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar Mono vacío con subject null")
    void shouldReturnEmptyMonoWithNullSubject() {
        // Given
        String token = createTokenWithNullSubject();
        Authentication auth = new UsernamePasswordAuthenticationToken(null, token, null);

        // When
        Mono<Authentication> result = authenticationManager.authenticate(auth);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar Mono vacío con subject vacío")
    void shouldReturnEmptyMonoWithEmptySubject() {
        // Given
        String token = createTokenWithEmptySubject();
        Authentication auth = new UsernamePasswordAuthenticationToken(null, token, null);

        // When
        Mono<Authentication> result = authenticationManager.authenticate(auth);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe mapear correctamente roleId a nombres de roles")
    void shouldMapRoleIdToRoleNamesCorrectly() {
        // Given
        String userId = "user123";
        String token = createTokenWithRoleId(userId, "3");
        Authentication auth = new UsernamePasswordAuthenticationToken(null, token, null);

        // When
        Mono<Authentication> result = authenticationManager.authenticate(auth);

        // Then
        StepVerifier.create(result)
                .assertNext(authentication -> {
                    assertEquals(userId, authentication.getPrincipal());
                    assertEquals(1, authentication.getAuthorities().size());
                    assertTrue(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe mapear roleId ASESOR correctamente")
    void shouldMapRoleIdAsesorCorrectly() {
        // Given
        String userId = "user123";
        String token = createTokenWithRoleId(userId, "2");
        Authentication auth = new UsernamePasswordAuthenticationToken(null, token, null);

        // When
        Mono<Authentication> result = authenticationManager.authenticate(auth);

        // Then
        StepVerifier.create(result)
                .assertNext(authentication -> {
                    assertTrue(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ASESOR")));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe mapear roleId CLIENTE correctamente")
    void shouldMapRoleIdClienteCorrectly() {
        // Given
        String userId = "user123";
        String token = createTokenWithRoleId(userId, "1");
        Authentication auth = new UsernamePasswordAuthenticationToken(null, token, null);

        // When
        Mono<Authentication> result = authenticationManager.authenticate(auth);

        // Then
        StepVerifier.create(result)
                .assertNext(authentication -> {
                    assertTrue(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CLIENTE")));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar roleId desconocido")
    void shouldHandleUnknownRoleId() {
        // Given
        String userId = "user123";
        String token = createTokenWithRoleId(userId, "99");
        Authentication auth = new UsernamePasswordAuthenticationToken(null, token, null);

        // When
        Mono<Authentication> result = authenticationManager.authenticate(auth);

        // Then
        StepVerifier.create(result)
                .assertNext(authentication -> {
                    assertEquals(0, authentication.getAuthorities().size());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar token sin roles ni roleId")
    void shouldHandleTokenWithoutRolesAndRoleId() {
        // Given
        String userId = "user123";
        String token = createTokenWithoutRoles(userId);
        Authentication auth = new UsernamePasswordAuthenticationToken(null, token, null);

        // When
        Mono<Authentication> result = authenticationManager.authenticate(auth);

        // Then
        StepVerifier.create(result)
                .assertNext(authentication -> {
                    assertEquals(userId, authentication.getPrincipal());
                    assertEquals(0, authentication.getAuthorities().size());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe preferir roles sobre roleId cuando ambos están presentes")
    void shouldPreferRolesOverRoleIdWhenBothPresent() {
        // Given
        String userId = "user123";
        List<String> roles = List.of("CUSTOM_ROLE");
        String token = createTokenWithRolesAndRoleId(userId, roles, "3");
        Authentication auth = new UsernamePasswordAuthenticationToken(null, token, null);

        // When
        Mono<Authentication> result = authenticationManager.authenticate(auth);

        // Then
        StepVerifier.create(result)
                .assertNext(authentication -> {
                    assertEquals(1, authentication.getAuthorities().size());
                    assertTrue(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CUSTOM_ROLE")));
                    assertFalse(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
                })
                .verifyComplete();
    }

    // Helper methods for creating test tokens

    private String createValidToken(String userId, List<String> roles) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuer(validIssuer)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .signWith(secretKey)
                .compact();
    }

    private String createExpiredToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuer(validIssuer)
                .setIssuedAt(Date.from(Instant.now().minus(2, ChronoUnit.HOURS)))
                .setExpiration(Date.from(Instant.now().minus(1, ChronoUnit.HOURS)))
                .signWith(secretKey)
                .compact();
    }

    private String createTokenWithIssuer(String userId, String issuer) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .signWith(secretKey)
                .compact();
    }

    private String createTokenWithoutIssuer(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .signWith(secretKey)
                .compact();
    }

    private String createTokenWithNullSubject() {
        return Jwts.builder()
                .setSubject(null)
                .setIssuer(validIssuer)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .signWith(secretKey)
                .compact();
    }

    private String createTokenWithEmptySubject() {
        return Jwts.builder()
                .setSubject("")
                .setIssuer(validIssuer)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .signWith(secretKey)
                .compact();
    }

    private String createTokenWithRoleId(String userId, String roleId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuer(validIssuer)
                .claim("roleId", roleId)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .signWith(secretKey)
                .compact();
    }

    private String createTokenWithoutRoles(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuer(validIssuer)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .signWith(secretKey)
                .compact();
    }

    private String createTokenWithRolesAndRoleId(String userId, List<String> roles, String roleId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuer(validIssuer)
                .claim("roles", roles)
                .claim("roleId", roleId)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .signWith(secretKey)
                .compact();
    }
}