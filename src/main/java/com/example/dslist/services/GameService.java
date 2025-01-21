package com.example.dslist.services;

import com.example.dslist.Mappers.GameMapper;
import com.example.dslist.Mappers.GameMinMapper;
import com.example.dslist.Mappers.GamesListMinMapper;
import com.example.dslist.dto.GameDTO;
import com.example.dslist.dto.GameMinDTO;
import com.example.dslist.dto.GamesListMinDTO;
import com.example.dslist.entities.Game;
import com.example.dslist.projections.GameMinProjection;
import com.example.dslist.repositories.GameRepository;
import com.example.dslist.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameMapper gameMapper;

    @Autowired
    GameMinMapper gameMinMapper;

    @Autowired
    GamesListMinMapper gamesListMinMapper;

    @Transactional(readOnly = true)
    public List<GameMinDTO> findAll() {
        List<Game> result = gameRepository.findAll();
//        return result.stream()
//                .map(x -> new GameMinDTO(x))
//                .toList();

        return result.stream()
                .map(x -> gameMinMapper.gameToGameMinDTO(x))
                .toList();
    }

    @Transactional(readOnly = true)
    public GameDTO findById(Long id){
        Game result = gameRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Game not found"));
        return gameMapper.gameToGameDTO(result);
    }

    @Transactional(readOnly = true)
    public List<GamesListMinDTO> findByList(Long listId) {
        List<GameMinProjection> result = gameRepository.searchByList(listId);
        return result.stream()
                .map(x ->  gamesListMinMapper.projectionToDto(x))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GamesListMinDTO> findByListJPQL(Long listId) {
        List<GamesListMinDTO> result = gameRepository.searchByListJPQL(listId);
        return result;
    }

}
