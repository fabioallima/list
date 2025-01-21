package com.example.dslist.Mappers;

import com.example.dslist.dto.GameDTO;
import com.example.dslist.entities.Game;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameMapper {
    GameDTO gameToGameDTO(Game game);
    Game gameDTOToGame(GameDTO gameDTO);
}
