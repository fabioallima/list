package com.example.dslist.tests;

import com.example.dslist.dto.RoleDTO;
import com.example.dslist.dto.UserDTO;
import com.example.dslist.dto.UserInsertDTO;
import com.example.dslist.dto.UserUpdateDTO;
import com.example.dslist.entities.Role;
import com.example.dslist.entities.User;
import com.example.dslist.projections.UserDetailsProjection;

import java.util.List;

public class UserFactory {

    public static User createUser() {
        return new User(1L, "John", "Doe", "john@example.com", "password");
    }

    public static UserDTO createUserDTO() {
        return new UserDTO(createUser());
    }

    public static UserInsertDTO createUserInsertDTO() {
        UserInsertDTO dto = new UserInsertDTO();
        dto.setFirstName("Jane");
        dto.setLastName("Doe");
        dto.setEmail("jane@example.com");
        dto.setPassword("password");
        return dto;
    }

    public static UserUpdateDTO createUserUpdateDTO() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setFirstName("John");
        dto.setLastName("Smith");
        dto.setEmail("john@example.com");
        return dto;
    }

    public static Role createRole() {
        return new Role(1L, "ROLE_OPERATOR");
    }

    public static UserInsertDTO createUserInsertDTO(List<RoleDTO> roles) {
        UserInsertDTO userInsertDTO;
        Role role;

        userInsertDTO = new UserInsertDTO();
        userInsertDTO.setFirstName("Jane");
        userInsertDTO.setLastName("Doe");
        userInsertDTO.setEmail("jane@example.com");
        userInsertDTO.setPassword("Password123@");
        role = new Role(1L, "ROLE_OPERATOR");

        return userInsertDTO;
    }

    public static UserUpdateDTO createUserUpdateDTO(List<RoleDTO> roles) {
        UserUpdateDTO userUpdateDTO;
        Role role;

        userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setFirstName("John");
        userUpdateDTO.setLastName("Smith");
        userUpdateDTO.setEmail("john@example.com");
        role = new Role(1L, "ROLE_OPERATOR");

        return userUpdateDTO;
    }

    public static UserDetailsProjection createUserDetailsProjection(){
        return new UserDetailsProjection() {
            public Long getId() { return 1L; }
            public String getUsername() { return "test@example.com"; }
            public String getPassword() { return "password"; }
            public Long getRoleId() { return 1L; }
            public String getAuthority() { return "ROLE_OPERATOR"; }
        };
    }
}

