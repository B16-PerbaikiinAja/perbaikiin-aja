package id.ac.ui.cs.advprog.perbaikiinaja.dtos.auth;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LoginResponseDtoTest {

    @Test
    void testDefaultConstructor() {
        // Act
        LoginResponseDto dto = new LoginResponseDto();

        // Assert
        assertNull(dto.getToken());
        assertEquals(0L, dto.getExpiresIn());
        assertNull(dto.getRole());
        assertNull(dto.getFullName());
        assertNull(dto.getEmail());
        assertNull(dto.getId());
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        LoginResponseDto dto = new LoginResponseDto();
        String token = "jwt-token-123";
        long expiresIn = 3600L;
        String role = "CUSTOMER";
        String fullName = "John Doe";
        String email = "john.doe@example.com";
        UUID id = UUID.randomUUID();

        // Act
        dto.setToken(token);
        dto.setExpiresIn(expiresIn);
        dto.setRole(role);
        dto.setFullName(fullName);
        dto.setEmail(email);
        dto.setId(id);

        // Assert
        assertEquals(token, dto.getToken());
        assertEquals(expiresIn, dto.getExpiresIn());
        assertEquals(role, dto.getRole());
        assertEquals(fullName, dto.getFullName());
        assertEquals(email, dto.getEmail());
        assertEquals(id, dto.getId());
    }

    @Test
    void testSettersWithNullValues() {
        // Arrange
        LoginResponseDto dto = new LoginResponseDto();

        // First set non-null values
        dto.setToken("token");
        dto.setRole("ADMIN");
        dto.setFullName("Admin User");
        dto.setEmail("admin@example.com");
        dto.setId(UUID.randomUUID());

        // Act - set null values
        dto.setToken(null);
        dto.setRole(null);
        dto.setFullName(null);
        dto.setEmail(null);
        dto.setId(null);

        // Assert
        assertNull(dto.getToken());
        assertNull(dto.getRole());
        assertNull(dto.getFullName());
        assertNull(dto.getEmail());
        assertNull(dto.getId());
    }
}