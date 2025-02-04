package com.example.dslist.controllers;

import com.example.dslist.config.TestSecurityConfig;
import com.example.dslist.dto.EmailDTO;
import com.example.dslist.dto.NewPasswordDTO;
import com.example.dslist.services.AuthService;
import com.example.dslist.services.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({TestSecurityConfig.class, AuthService.class})
public class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmailDTO validEmailDTO;
    private EmailDTO invalidEmailDTO;
    private NewPasswordDTO validNewPasswordDTO;
    private NewPasswordDTO invalidNewPasswordDTO;

    @BeforeEach
    void setUp() {
        validEmailDTO = new EmailDTO("valid@example.com");
        invalidEmailDTO = new EmailDTO("invalid@example.com");
        validNewPasswordDTO = new NewPasswordDTO("validToken", "v66D~}4@wN5*.a&*nv%p");
        invalidNewPasswordDTO = new NewPasswordDTO("invalidToken", "v66D~}4@wN5*.a&*nv%p");

        doNothing().when(authService).createRecoverToken(eq(validEmailDTO));
        doThrow(ResourceNotFoundException.class).when(authService).createRecoverToken(eq(invalidEmailDTO));

        doNothing().when(authService).saveNewPassword(eq(validNewPasswordDTO));
        doThrow(ResourceNotFoundException.class).when(authService).saveNewPassword(eq(invalidNewPasswordDTO));
    }

    @Test
    public void createRecoverToken_ShouldReturnNoContent_WhenEmailIsValid() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(validEmailDTO);

        ResultActions result =
                mockMvc.perform(post("/auth/recover-token")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
        verify(authService, times(1)).createRecoverToken(any(EmailDTO.class));
    }

    @Test
    public void createRecoverToken_ShouldReturnNotFound_WhenEmailIsInvalid() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(invalidEmailDTO);

        ResultActions result =
                mockMvc.perform(post("/auth/recover-token")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
        verify(authService, times(1)).createRecoverToken(any(EmailDTO.class));
    }

    @Test
    public void saveNewPassword_ShouldReturnNoContent_WhenNewPasswordIsValid() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(validNewPasswordDTO);

        ResultActions result =
                mockMvc.perform(put("/auth/new-password")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
        verify(authService, times(1)).saveNewPassword(any(NewPasswordDTO.class));
    }

    @Test
    public void saveNewPassword_ShouldReturnNotFound_WhenNewPasswordIsInvalid() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(invalidNewPasswordDTO);

        ResultActions result =
                mockMvc.perform(put("/auth/new-password")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
        verify(authService, times(1)).saveNewPassword(any(NewPasswordDTO.class));
    }
}
