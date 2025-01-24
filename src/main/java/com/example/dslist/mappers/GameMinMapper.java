package com.example.dslist.mappers;

import com.example.dslist.dto.GameMinDTO;
import com.example.dslist.entities.Game;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GameMinMapper {
    //@Mapping(target = "position", ignore = true)
    GameMinDTO gameToGameMinDTO(Game game);
}
