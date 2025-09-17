package com.crediya.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtProperties Tests")
class JwtPropertiesTest {

    @Test
    @DisplayName("Debe crear JwtProperties con constructor por defecto")
    void shouldCreateJwtPropertiesWithDefaultConstructor() {
        // Given & When
        JwtProperties properties = new JwtProperties();

        // Then
        assertNotNull(properties);
        assertNull(properties.getSecret());
        assertNull(properties.getIssuer());
        assertNull(properties.getExpirationSec());
    }

    @Test
    @DisplayName("Debe permitir establecer y obtener secret")
    void shouldAllowSettingAndGettingSecret() {
        // Given
        JwtProperties properties = new JwtProperties();
        String secret = "QnE1T2lXbVRhV3RzR2VOUXlHaFZ2d2dyU2p2a1R2TnM=";

        // When
        properties.setSecret(secret);

        // Then
        assertEquals(secret, properties.getSecret());
    }

    @Test
    @DisplayName("Debe permitir establecer y obtener issuer")
    void shouldAllowSettingAndGettingIssuer() {
        // Given
        JwtProperties properties = new JwtProperties();
        String issuer = "autenticacion-service";

        // When
        properties.setIssuer(issuer);

        // Then
        assertEquals(issuer, properties.getIssuer());
    }

    @Test
    @DisplayName("Debe permitir establecer y obtener expirationSec")
    void shouldAllowSettingAndGettingExpirationSec() {
        // Given
        JwtProperties properties = new JwtProperties();
        Long expirationSec = 3600L;

        // When
        properties.setExpirationSec(expirationSec);

        // Then
        assertEquals(expirationSec, properties.getExpirationSec());
    }

    @Test
    @DisplayName("Debe manejar valores null correctamente")
    void shouldHandleNullValuesCorrectly() {
        // Given
        JwtProperties properties = new JwtProperties();

        // When
        properties.setSecret(null);
        properties.setIssuer(null);
        properties.setExpirationSec(null);

        // Then
        assertNull(properties.getSecret());
        assertNull(properties.getIssuer());
        assertNull(properties.getExpirationSec());
    }

    @Test
    @DisplayName("Debe manejar strings vacíos correctamente")
    void shouldHandleEmptyStringsCorrectly() {
        // Given
        JwtProperties properties = new JwtProperties();
        String emptyString = "";

        // When
        properties.setSecret(emptyString);
        properties.setIssuer(emptyString);

        // Then
        assertEquals(emptyString, properties.getSecret());
        assertEquals(emptyString, properties.getIssuer());
    }

    @Test
    @DisplayName("Debe manejar valores extremos de expirationSec")
    void shouldHandleExtremeExpirationSecValues() {
        // Given
        JwtProperties properties = new JwtProperties();

        // When & Then - Max value
        properties.setExpirationSec(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, properties.getExpirationSec());

        // When & Then - Min value
        properties.setExpirationSec(Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE, properties.getExpirationSec());

        // When & Then - Zero
        properties.setExpirationSec(0L);
        assertEquals(0L, properties.getExpirationSec());

        // When & Then - Negative value
        properties.setExpirationSec(-1L);
        assertEquals(-1L, properties.getExpirationSec());
    }

    @Test
    @DisplayName("Debe permitir establecer todos los campos a la vez")
    void shouldAllowSettingAllFieldsAtOnce() {
        // Given
        JwtProperties properties = new JwtProperties();
        String secret = "test-secret-key";
        String issuer = "test-issuer";
        Long expirationSec = 7200L;

        // When
        properties.setSecret(secret);
        properties.setIssuer(issuer);
        properties.setExpirationSec(expirationSec);

        // Then
        assertEquals(secret, properties.getSecret());
        assertEquals(issuer, properties.getIssuer());
        assertEquals(expirationSec, properties.getExpirationSec());
    }

    @Test
    @DisplayName("Debe tener anotación @ConfigurationProperties con prefix correcto")
    void shouldHaveConfigurationPropertiesAnnotationWithCorrectPrefix() {
        // Given & When
        ConfigurationProperties annotation = JwtProperties.class.getAnnotation(ConfigurationProperties.class);

        // Then
        assertNotNull(annotation);
        assertEquals("security", annotation.prefix());
    }

    @Test
    @DisplayName("Debe manejar secret con caracteres especiales")
    void shouldHandleSecretWithSpecialCharacters() {
        // Given
        JwtProperties properties = new JwtProperties();
        String secretWithSpecialChars = "MySecret!@#$%^&*()_+-={}[]|\\:;\"'<>,.?/~`";

        // When
        properties.setSecret(secretWithSpecialChars);

        // Then
        assertEquals(secretWithSpecialChars, properties.getSecret());
    }

    @Test
    @DisplayName("Debe manejar issuer con espacios y caracteres especiales")
    void shouldHandleIssuerWithSpacesAndSpecialCharacters() {
        // Given
        JwtProperties properties = new JwtProperties();
        String issuerWithSpaces = "My Service - Auth Provider 2024";

        // When
        properties.setIssuer(issuerWithSpaces);

        // Then
        assertEquals(issuerWithSpaces, properties.getIssuer());
    }

    @Test
    @DisplayName("Debe manejar secret muy largo")
    void shouldHandleVeryLongSecret() {
        // Given
        JwtProperties properties = new JwtProperties();
        StringBuilder longSecret = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longSecret.append("a");
        }
        String veryLongSecret = longSecret.toString();

        // When
        properties.setSecret(veryLongSecret);

        // Then
        assertEquals(veryLongSecret, properties.getSecret());
        assertEquals(1000, properties.getSecret().length());
    }

    @Test
    @DisplayName("Debe validar que los getters y setters existen")
    void shouldValidateGettersAndSettersExist() throws NoSuchMethodException {
        // Given
        Class<JwtProperties> clazz = JwtProperties.class;

        // When & Then - Secret
        Method getSecret = clazz.getMethod("getSecret");
        Method setSecret = clazz.getMethod("setSecret", String.class);
        assertNotNull(getSecret);
        assertNotNull(setSecret);

        // When & Then - Issuer
        Method getIssuer = clazz.getMethod("getIssuer");
        Method setIssuer = clazz.getMethod("setIssuer", String.class);
        assertNotNull(getIssuer);
        assertNotNull(setIssuer);

        // When & Then - ExpirationSec
        Method getExpirationSec = clazz.getMethod("getExpirationSec");
        Method setExpirationSec = clazz.getMethod("setExpirationSec", Long.class);
        assertNotNull(getExpirationSec);
        assertNotNull(setExpirationSec);
    }

    @Test
    @DisplayName("Debe permitir múltiples modificaciones del mismo campo")
    void shouldAllowMultipleModificationsOfSameField() {
        // Given
        JwtProperties properties = new JwtProperties();

        // When & Then - Multiple secret changes
        properties.setSecret("secret1");
        assertEquals("secret1", properties.getSecret());

        properties.setSecret("secret2");
        assertEquals("secret2", properties.getSecret());

        properties.setSecret("secret3");
        assertEquals("secret3", properties.getSecret());

        // When & Then - Multiple issuer changes
        properties.setIssuer("issuer1");
        assertEquals("issuer1", properties.getIssuer());

        properties.setIssuer("issuer2");
        assertEquals("issuer2", properties.getIssuer());

        // When & Then - Multiple expiration changes
        properties.setExpirationSec(1000L);
        assertEquals(1000L, properties.getExpirationSec());

        properties.setExpirationSec(2000L);
        assertEquals(2000L, properties.getExpirationSec());
    }

    @Test
    @DisplayName("Debe manejar expirationSec típicos")
    void shouldHandleTypicalExpirationSecValues() {
        // Given
        JwtProperties properties = new JwtProperties();

        // When & Then - 15 minutes
        properties.setExpirationSec(900L);
        assertEquals(900L, properties.getExpirationSec());

        // When & Then - 1 hour
        properties.setExpirationSec(3600L);
        assertEquals(3600L, properties.getExpirationSec());

        // When & Then - 1 day
        properties.setExpirationSec(86400L);
        assertEquals(86400L, properties.getExpirationSec());

        // When & Then - 1 week
        properties.setExpirationSec(604800L);
        assertEquals(604800L, properties.getExpirationSec());
    }

    @Test
    @DisplayName("Debe manejar configuración completa típica")
    void shouldHandleTypicalCompleteConfiguration() {
        // Given
        JwtProperties properties = new JwtProperties();
        String typicalSecret = "QnE1T2lXbVRhV3RzR2VOUXlHaFZ2d2dyU2p2a1R2TnM=";
        String typicalIssuer = "autenticacion-service";
        Long typicalExpiration = 3600L;

        // When
        properties.setSecret(typicalSecret);
        properties.setIssuer(typicalIssuer);
        properties.setExpirationSec(typicalExpiration);

        // Then
        assertEquals(typicalSecret, properties.getSecret());
        assertEquals(typicalIssuer, properties.getIssuer());
        assertEquals(typicalExpiration, properties.getExpirationSec());
        
        // Verify all fields are properly configured
        assertNotNull(properties.getSecret());
        assertNotNull(properties.getIssuer());
        assertNotNull(properties.getExpirationSec());
        assertTrue(properties.getSecret().length() > 32); // Typical JWT secret length
        assertTrue(properties.getExpirationSec() > 0); // Positive expiration time
    }
}