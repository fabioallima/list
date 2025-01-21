package com.example.dslist.services;

import com.example.dslist.Mappers.GameListMapper;
import com.example.dslist.Mappers.GameMapper;
import com.example.dslist.dto.GameDTO;
import com.example.dslist.dto.GameListDTO;
import com.example.dslist.dto.GameMinDTO;
import com.example.dslist.entities.Game;
import com.example.dslist.entities.GameList;
import com.example.dslist.repositories.GameListRepository;
import com.example.dslist.repositories.GameRepository;
import com.example.dslist.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GameListService {

    @Autowired
    private GameListRepository gameListRepository;

    @Autowired
    private GameListMapper gameListMapper;

    @Transactional(readOnly = true)
    public List<GameListDTO> findAll() {
        List<GameList> result = gameListRepository.findAll();
        return result.stream()
                .map(x -> gameListMapper.gameListToGameListDTO(x))
                .toList();
    }

}
