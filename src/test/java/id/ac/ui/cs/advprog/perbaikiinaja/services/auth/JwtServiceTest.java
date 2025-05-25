package id.ac.ui.cs.advprog.perbaikiinaja.services.auth;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private final String secretKey = "ZmFrZXNlY3JldGtleWZha2VzZWNyZXRrZXl0b29sb25nZm9ydGVzdA==";
    private final long expiration = 1000 * 60 * 60; // 1 hour

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", expiration);
    }

    @Test
    void generateAndValidateToken() {
        UserDetails userDetails = new User("user@mail.com", "password", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertEquals("user@mail.com", jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void generateTokenWithExtraClaims() {
        UserDetails userDetails = new User("user@mail.com", "password", Collections.emptyList());
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");
        String token = jwtService.generateToken(claims, userDetails);

        assertNotNull(token);
        assertEquals("user@mail.com", jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token, userDetails));
        assertEquals("ADMIN", jwtService.extractClaim(token, claimsMap -> claimsMap.get("role")));
    }

    @Test
    void getExpirationTime_returnsConfiguredValue() {
        assertEquals(expiration, jwtService.getExpirationTime());
    }

    @Test
    void isTokenValid_returnsFalseForWrongUser() {
        UserDetails userDetails = new User("user@mail.com", "password", Collections.emptyList());
        UserDetails otherUser = new User("other@mail.com", "password", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

    @Test
    void isTokenExpired_returnsTrueForExpiredToken() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L);
        UserDetails userDetails = new User("user@mail.com", "password", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () -> jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void extractClaim_returnsSubject() {
        UserDetails userDetails = new User("user@mail.com", "password", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        assertTrue(jwtService.isTokenValid(token, userDetails));
        String subject = jwtService.extractClaim(token, Claims::getSubject);
        assertEquals("user@mail.com", subject);

        String constant = jwtService.extractClaim(token, claims -> "constant");
        assertEquals("constant", constant);

        Object nullResult = jwtService.extractClaim(token, claims -> null);
        assertNull(nullResult);
    }

    @Test
    void extractExpiration_returnsExpirationDate() {
        UserDetails userDetails = new User("user@mail.com", "password", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        assertNotNull(jwtService.extractClaim(token, Claims::getExpiration));
    }

    @Test
    void buildToken_setsClaimsAndExpiration() {
        UserDetails userDetails = new User("user@mail.com", "password", Collections.emptyList());
        Map<String, Object> claims = new HashMap<>();
        claims.put("foo", "bar");
        String token = ReflectionTestUtils.invokeMethod(jwtService, "buildToken", claims, userDetails, expiration);

        assertNotNull(token);
        assertEquals("bar", jwtService.extractClaim(token, c -> c.get("foo")));
    }

    @Test
    void getSignInKey_returnsKey() {
        assertNotNull(ReflectionTestUtils.invokeMethod(jwtService, "getSignInKey"));
    }
}
