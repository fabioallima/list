package com.example.dslist.services;

import com.example.dslist.services.exceptions.EmailException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTests {
    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String emailFrom;

    @BeforeEach
    void setUp() {
        //MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(emailService, "emailFrom", emailFrom);
    }

    @Test
    void sendEmail_SuccessfulSend_ShouldNotThrowException() {
        // Arrange
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        assertDoesNotThrow(() -> emailService.sendEmail(to, subject, body));

        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_FailedSend_ShouldThrowEmailException() {
        // Arrange
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        doThrow(new MailException("Mail error") {}).when(emailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        EmailException exception = assertThrows(EmailException.class,
                () -> emailService.sendEmail(to, subject, body));

        assertEquals("Failed to send email", exception.getMessage());
        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
