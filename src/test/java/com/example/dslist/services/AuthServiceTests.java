package com.example.dslist.services;

import com.example.dslist.dto.EmailDTO;
import com.example.dslist.dto.NewPasswordDTO;
import com.example.dslist.entities.PasswordRecover;
import com.example.dslist.entities.User;
import com.example.dslist.repositories.PasswordRecoverRepository;
import com.example.dslist.repositories.UserRepository;
import com.example.dslist.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthServiceTests {

    @InjectMocks
    private AuthService authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordRecoverRepository passwordRecoverRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private UserRepository userRepository;

    private String email;

    @BeforeEach
    void setUp() {
        email = "test@example.com";
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(authService, "tokenMinutes", 30L);
        ReflectionTestUtils.setField(authService, "recoverUri", "http://example.com/recover/");
    }

    @Test
    void createRecoverToken_ValidEmail_ShouldCreateTokenAndSendEmail() {
        // Arrange
        EmailDTO emailDTO = new EmailDTO(email);
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(emailDTO.email())).thenReturn(user);
        when(passwordRecoverRepository.save(any(PasswordRecover.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        assertDoesNotThrow(() -> authService.createRecoverToken(emailDTO));

        // Assert
        verify(passwordRecoverRepository).save(any(PasswordRecover.class));
        verify(emailService).sendEmail(eq(email), eq("Recuperação de Senha"), anyString());
    }

    @Test
    void createRecoverToken_InvalidEmail_ShouldThrowResourceNotFoundException() {
        // Arrange
        EmailDTO emailDTO = new EmailDTO("nonexistent@example.com");
        when(userRepository.findByEmail(emailDTO.email())).thenReturn(null);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> authService.createRecoverToken(emailDTO));
    }

    @Test
    void saveNewPassword_ValidToken_ShouldUpdatePassword() {
        // Arrange
        NewPasswordDTO newPasswordDTO = new NewPasswordDTO("validToken", "newPassword");
        PasswordRecover passwordRecover = new PasswordRecover();
        passwordRecover.setEmail(email);
        User user = new User();
        user.setEmail(email);

        when(passwordRecoverRepository.searchValidTokens(eq("validToken"), any(Instant.class)))
                .thenReturn(Collections.singletonList(passwordRecover));
        when(userRepository.findByEmail(email)).thenReturn(user);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        assertDoesNotThrow(() -> authService.saveNewPassword(newPasswordDTO));

        // Assert
        verify(userRepository).save(argThat(savedUser ->
                savedUser.getPassword().equals("encodedPassword")
        ));
    }

    @Test
    void saveNewPassword_InvalidToken_ShouldThrowResourceNotFoundException() {
        // Arrange
        NewPasswordDTO newPasswordDTO = new NewPasswordDTO("invalidToken", "newPassword");
        when(passwordRecoverRepository.searchValidTokens(eq("invalidToken"), any(Instant.class)))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> authService.saveNewPassword(newPasswordDTO));
    }

    @Test
    void authenticated_ValidUser_ShouldReturnUser() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Jwt jwt = mock(Jwt.class);
        User user = new User();
        user.setEmail(email);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaim("username")).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(user);

        // Act
        User result = authService.authenticated();

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void authenticated_InvalidUser_ShouldThrowUsernameNotFoundException() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Jwt jwt = mock(Jwt.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaim("username")).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(null); // Simula usuário não encontrado

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> authService.authenticated());

        // Verify
        verify(userRepository).findByEmail(email);
    }
}

