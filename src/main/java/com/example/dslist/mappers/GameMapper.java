package com.example.dslist.mappers;

import com.example.dslist.dto.GameDTO;
import com.example.dslist.entities.Game;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GameMapper {
    GameDTO gameToGameDTO(Game game);
    Game gameDTOToGame(GameDTO gameDTO);
}
