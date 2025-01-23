package com.example.dslist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NewPasswordDTO(
        @NotBlank(message = "Campo Obrigatório")
        String token,

        @NotBlank(message = "Campo Obrigatório")
        @Size(min = 8, message = "Deve ter no mínimo 8 caracteres")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$", message = "Senha Fraca")
        String password
) {}
