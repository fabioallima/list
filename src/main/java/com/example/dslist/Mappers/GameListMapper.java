package com.example.dslist.Mappers;

import com.example.dslist.dto.GameListDTO;
import com.example.dslist.entities.GameList;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GameListMapper {
    GameListDTO gameListToGameListDTO(GameList gameList);
    GameList gameListDTOToGameList(GameListDTO gameListDTO);
}
