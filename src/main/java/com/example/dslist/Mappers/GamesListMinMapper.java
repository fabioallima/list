package com.example.dslist.Mappers;

import com.example.dslist.dto.GameMinDTO;
import com.example.dslist.dto.GamesListMinDTO;
import com.example.dslist.entities.Game;
import com.example.dslist.projections.GameMinProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GamesListMinMapper {
    GamesListMinDTO projectionToDto(GameMinProjection projection);
}
