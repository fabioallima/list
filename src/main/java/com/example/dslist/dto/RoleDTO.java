package com.example.dslist.dto;

import com.example.dslist.entities.Role;

import java.io.Serializable;

public record RoleDTO(Long id, String authority) implements Serializable {
    public RoleDTO {
    }

    public RoleDTO(Role role) {
        this(role.getId(), role.getAuthority());
    }


    public Long getId() {
        return id;
    }

    public String getAuthority() {
        return authority;
    }
}
