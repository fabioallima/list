package com.example.dslist.controllers;

import com.example.dslist.dto.UserDTO;
import com.example.dslist.dto.UserInsertDTO;
import com.example.dslist.dto.UserUpdateDTO;
import com.example.dslist.entities.User;
import com.example.dslist.repositories.UserRepository;
import com.example.dslist.services.UserService;
import com.example.dslist.services.exceptions.ResourceNotFoundException;
import com.example.dslist.services.validation.UserInsertValidator;
import com.example.dslist.services.validation.UserUpdateValidator;
import com.example.dslist.tests.UserFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {UserController.class, UserService.class, UserInsertValidator.class}, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@ComponentScan(basePackages = "com.example.dslist.services.validation")
public class UserControllerTests {

    private static final String URI = "/users";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService service;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserUpdateValidator userUpdateValidator;

    @Autowired
    private ObjectMapper objectMapper;

    private Long existingId;
    private Long nonExistingId;
    private String existingEmail;
    private String newEmail;
    private UserDTO userDTO;
    private UserInsertDTO userInsertDTO;
    private UserUpdateDTO userUpdateDTO;
    private PageImpl<UserDTO> page;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;

        existingEmail = "existing@example.com";
        newEmail = "new@example.com";

        userDTO = UserFactory.createUserDTO();
        userInsertDTO = UserFactory.createUserInsertDTO();
        userUpdateDTO = UserFactory.createUserUpdateDTO();
        page = new PageImpl<>(List.of(userDTO));

        when(service.findAllPaged(any())).thenReturn(page);

        when(service.findById(existingId)).thenReturn(userDTO);
        when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        when(userRepository.findByEmail(eq(existingEmail))).thenReturn(new User());
        when(userRepository.findByEmail(eq(newEmail))).thenReturn(null);

        when(service.findProfile()).thenReturn(userDTO);

        when(service.insert(any())).thenReturn(userDTO);

        when(service.update(eq(existingId), any())).thenReturn(userDTO);
        when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);

        doNothing().when(service).delete(existingId);
        doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
    }

    @Test
    public void findAll_ShouldReturnPage() throws Exception {
        ResultActions result =
                mockMvc.perform(get(URI)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content").exists());
    }

    @Test
    public void findById_ShouldReturnUser_WhenIdExists() throws Exception {
        ResultActions result =
                mockMvc.perform(get(URI + "/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.firstName").exists());
    }

    @Test
    public void findById_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
        ResultActions result =
                mockMvc.perform(get(URI + "/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void findProfile_ShouldReturnUser() throws Exception {
        ResultActions result =
                mockMvc.perform(get(URI + "/profile")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.firstName").exists());
    }

    @Test
    public void insert_ShouldReturnCreatedUserDTO() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(userInsertDTO);

        ResultActions result =
                mockMvc.perform(post(URI)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.email").exists());
    }

    @Test
    public void insert_ShouldReturnBadRequest_WhenEmailAlreadyExists() throws Exception {
        UserInsertDTO insertDTO = UserFactory.createUserInsertDTO();
        insertDTO.setEmail(existingEmail);
        String jsonBody = objectMapper.writeValueAsString(insertDTO);

        ResultActions result =
                mockMvc.perform(post(URI)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(jsonPath("$.errors[0].fieldName").value("email"));
        result.andExpect(jsonPath("$.errors[0].message").value("Email já existe"));
    }


    @Test
    public void update_ShouldReturnUserDTO_WhenIdExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(userUpdateDTO);

        ResultActions result =
                mockMvc.perform(put(URI + "/{id}", existingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.firstName").exists());
    }

    @Test
    public void update_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(userUpdateDTO);

        ResultActions result =
                mockMvc.perform(put(URI + "/{id}", nonExistingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void delete_ShouldReturnNoContent_WhenIdExists() throws Exception {
        ResultActions result =
                mockMvc.perform(delete(URI + "/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }

    @Test
    public void delete_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
        ResultActions result =
                mockMvc.perform(delete(URI + "/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }
}
