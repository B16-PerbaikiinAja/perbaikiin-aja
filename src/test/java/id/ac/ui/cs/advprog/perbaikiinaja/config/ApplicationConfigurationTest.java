package id.ac.ui.cs.advprog.perbaikiinaja.config;

import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.auth.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApplicationConfigurationTest {

    private UserRepository userRepository;
    private ApplicationConfiguration config;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        config = new ApplicationConfiguration(userRepository);
    }

    @Test
    void userDetailsServiceReturnsUser() {
        User user = mock(User.class);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        UserDetailsService uds = config.userDetailsService();
        assertEquals(user, uds.loadUserByUsername("test@example.com"));
    }

    @Test
    void userDetailsServiceThrowsIfNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        UserDetailsService uds = config.userDetailsService();
        assertThrows(UsernameNotFoundException.class, () -> uds.loadUserByUsername("notfound@example.com"));
    }

    @Test
    void passwordEncoderIsBCrypt() {
        assertTrue(config.passwordEncoder() instanceof BCryptPasswordEncoder);
    }

    @Test
    void authenticationProviderIsDao() {
        AuthenticationProvider provider = config.authenticationProvider();
        assertNotNull(provider);
    }

    @Test
    void authenticationManagerBean() throws Exception {
        AuthenticationConfiguration authConfig = mock(AuthenticationConfiguration.class);
        AuthenticationManager manager = mock(AuthenticationManager.class);
        when(authConfig.getAuthenticationManager()).thenReturn(manager);
        assertEquals(manager, config.authenticationManager(authConfig));
    }
}
