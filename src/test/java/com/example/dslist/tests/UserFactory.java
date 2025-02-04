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
        return new User(1L, "Paulo", "Pereira", "paulo@example.com", "v66D~}4@wN5*.a&*nv%p");
    }

    public static UserDTO createUserDTO() {
        return new UserDTO(createUser());
    }

    public static UserInsertDTO createUserInsertDTO() {
        UserInsertDTO dto = new UserInsertDTO();
        dto.setFirstName("Maria");
        dto.setLastName("Silva");
        dto.setEmail("maria@example.com");
        dto.setPassword("v66D~}4@wN5*.a&*nv%p");
        return dto;
    }

    public static UserUpdateDTO createUserUpdateDTO() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setFirstName("Paulo");
        dto.setLastName("Pereira");
        dto.setEmail("paulo@example.com");
        return dto;
    }

    public static Role createRole() {
        return new Role(1L, "ROLE_OPERATOR");
    }

    public static UserInsertDTO createUserInsertDTO(List<RoleDTO> roles) {
        UserInsertDTO userInsertDTO;

        userInsertDTO = new UserInsertDTO();
        userInsertDTO.setFirstName("Maria");
        userInsertDTO.setLastName("Silva");
        userInsertDTO.setEmail("maria@example.com");
        userInsertDTO.setPassword("v66D~}4@wN5*.a&*nv%p");
        userInsertDTO.getRoles().addAll(roles);

        return userInsertDTO;
    }

    public static UserUpdateDTO createUserUpdateDTO(List<RoleDTO> roles) {
        UserUpdateDTO userUpdateDTO;

        userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setFirstName("Paulo");
        userUpdateDTO.setLastName("Pereira");
        userUpdateDTO.setEmail("paulo@example.com");
        userUpdateDTO.getRoles().addAll(roles);

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

