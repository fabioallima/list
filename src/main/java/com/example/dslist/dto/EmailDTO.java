package com.example.dslist.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailDTO(
        @NotBlank(message = "Campo Obrigatório")
        @Email(message = "Favor entrar com um email válido")
        String email
) {}
