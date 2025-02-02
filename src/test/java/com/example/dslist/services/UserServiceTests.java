package com.example.dslist.services;

import com.example.dslist.dto.RoleDTO;
import com.example.dslist.dto.UserDTO;
import com.example.dslist.dto.UserInsertDTO;
import com.example.dslist.dto.UserUpdateDTO;
import com.example.dslist.entities.Role;
import com.example.dslist.entities.User;
import com.example.dslist.projections.UserDetailsProjection;
import com.example.dslist.repositories.RoleRepository;
import com.example.dslist.repositories.UserRepository;
import com.example.dslist.services.exceptions.DatabaseException;
import com.example.dslist.services.exceptions.ResourceNotFoundException;
import com.example.dslist.tests.UserFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthService authService;

    private User user;
    private UserInsertDTO userInsertDTO;
    private UserUpdateDTO userUpdateDTO;
    private User authenticatedUser;
    private Role role;

    @BeforeEach
    void setUp() {
        user = UserFactory.createUser();
        authenticatedUser = UserFactory.createUser();
        userInsertDTO = UserFactory.createUserInsertDTO();
        userUpdateDTO = UserFactory.createUserUpdateDTO();
        role = UserFactory.createRole();
    }

    @Test
    void findAllPagedShouldReturnPageOfUserDTO() {
        List<User> users = List.of(user);
        Page<User> page = new PageImpl<>(users);

        Mockito.when(userRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<UserDTO> result = userService.findAllPaged(PageRequest.of(0, 10));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getTotalElements());
    }

    @Test
    void findByIdShouldReturnUserDTOWhenIdExists() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO result = userService.findById(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(user.getId(), result.getId());
    }

    @Test
    void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.findById(99L));
    }

    @Test
    void insertShouldReturnUserDTO() {
        Mockito.when(roleRepository.findByAuthority("ROLE_OPERATOR")).thenReturn(role);
        Mockito.when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO result = userService.insert(userInsertDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(user.getId(), result.getId());
        Mockito.verify(userRepository).save(any(User.class));
    }

    @Test
    void updateShouldReturnUpdatedUserDTO() {
        Mockito.when(userRepository.getReferenceById(1L)).thenReturn(user);
        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO result = userService.update(1L, userUpdateDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(userUpdateDTO.getFirstName(), result.getFirstName());
        Assertions.assertEquals(userUpdateDTO.getLastName(), result.getLastName());
        Mockito.verify(userRepository).save(any(User.class));
    }

    @Test
    void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Mockito.when(userRepository.getReferenceById(99L)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.update(99L, userUpdateDTO));
    }

    @Test
    void insertShouldCopyAllFieldsFromDtoToEntity() {
        List<RoleDTO> roles = List.of(new RoleDTO(1L, "ROLE_OPERATOR"));
        UserInsertDTO insertDTO = UserFactory.createUserInsertDTO(roles);

        //User savedUser = new User();
        Role role = UserFactory.createRole();

        Mockito.when(roleRepository.findByAuthority("ROLE_OPERATOR")).thenReturn(role);
        Mockito.when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        Mockito.when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedEntity = invocation.getArgument(0);
            savedEntity.setId(1L);
            return savedEntity;
        });

        UserDTO result = userService.insert(insertDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(insertDTO.getFirstName(), result.getFirstName());
        Assertions.assertEquals(insertDTO.getLastName(), result.getLastName());
        Assertions.assertEquals(insertDTO.getEmail(), result.getEmail());
        Assertions.assertEquals(1, result.getRoles().size());

        Mockito.verify(roleRepository).findByAuthority("ROLE_OPERATOR");
        Mockito.verify(passwordEncoder).encode(insertDTO.getPassword());
        Mockito.verify(userRepository).save(any(User.class));
    }

    @Test
    void updateShouldCopyAllFieldsFromDtoToEntity() {
        // Arrange
        Long userId = 1L;
        List<RoleDTO> roles = List.of(new RoleDTO(1L, "ROLE_OPERATOR"), new RoleDTO(2L, "ROLE_ADMIN"));
        UserUpdateDTO updateDTO = UserFactory.createUserUpdateDTO(roles);

        User existingUser = UserFactory.createUser();
        //Role operatorRole = new Role(1L, "ROLE_OPERATOR");
        //Role adminRole = new Role(2L, "ROLE_ADMIN");

        Mockito.when(userRepository.getReferenceById(userId)).thenReturn(existingUser);
        Mockito.when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO result = userService.update(userId, updateDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(updateDTO.getFirstName(), result.getFirstName());
        Assertions.assertEquals(updateDTO.getLastName(), result.getLastName());
        Assertions.assertEquals(updateDTO.getEmail(), result.getEmail());

        verify(userRepository).getReferenceById(userId);
        verify(userRepository).save(any(User.class));
    }


    @Test
    void deleteShouldDoNothingWhenIdExists() {
        Long idToDelete = 1L;
        Mockito.doNothing().when(userRepository).deleteById(idToDelete);

        Assertions.assertDoesNotThrow(() -> userService.delete(idToDelete));

        Mockito.verify(userRepository, times(1)).deleteById(idToDelete);
    }

    @Test
    void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Long nonExistingId = 99L;
        Mockito.doThrow(EmptyResultDataAccessException.class).when(userRepository).deleteById(nonExistingId);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.delete(nonExistingId));
    }

    @Test
    void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        Long dependentId = 1L;
        Mockito.doThrow(DataIntegrityViolationException.class).when(userRepository).deleteById(dependentId);

        Assertions.assertThrows(DatabaseException.class, () -> userService.delete(dependentId));
    }

    @Test
    void loadUserByUsernameShouldReturnUserDetailsWhenUsernameExists() {

        UserDetailsProjection projection = UserFactory.createUserDetailsProjection();
        String existingUsername = projection.getUsername();

        List<UserDetailsProjection> projections = List.of(
                projection
        );

        Mockito.when(userRepository.searchUserAndRolesByEmail(existingUsername)).thenReturn(projections);

        UserDetails result = userService.loadUserByUsername(existingUsername);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingUsername, result.getUsername());
        Assertions.assertEquals("password", result.getPassword());
        Assertions.assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_OPERATOR")));
    }

    @Test
    void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUsernameDoesNotExist() {
        String nonExistingUsername = "nonexisting@example.com";
        Mockito.when(userRepository.searchUserAndRolesByEmail(nonExistingUsername)).thenReturn(Collections.emptyList());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(nonExistingUsername));
    }

    @Test
    void findProfileShouldReturnAuthenticatedUserDTO() {
        // Arrange
        when(authService.authenticated()).thenReturn(authenticatedUser);

        // Act
        UserDTO result = userService.findProfile();

        // Assert
        assertNotNull(result);
        assertEquals(authenticatedUser.getId(), result.getId());
        assertEquals(authenticatedUser.getFirstName(), result.getFirstName());
        assertEquals(authenticatedUser.getLastName(), result.getLastName());
        assertEquals(authenticatedUser.getEmail(), result.getEmail());
        // Verificar outros campos conforme necessário

        // Verify
        verify(authService, times(1)).authenticated();
    }
}