package com.example.dslist.dto;

public record GamesListMinDTO(
        Long id,
        String title,
        Integer year,
        String imgUrl,
        String shortDescription,
        Integer position
) {
}
