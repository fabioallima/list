package com.example.dslist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GameDTO(
        Long id,
        @Size(min = 3, max = 80, message = "Deve ter entre 5 e 80 caracteres")
        @NotBlank(message = "Campo Obrigatório")
        String title,
        Integer year,
        String genre,
        String platforms,
        Double score,
        String imgUrl,
        String shortDescription,
        String longDescription
) {
}
