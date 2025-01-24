package com.example.dslist.services;

import com.example.dslist.mappers.GameMapper;
import com.example.dslist.mappers.GameMinMapper;
import com.example.dslist.mappers.GamesListMinMapper;
import com.example.dslist.dto.GameDTO;
import com.example.dslist.dto.GameMinDTO;
import com.example.dslist.dto.GamesListMinDTO;
import com.example.dslist.entities.Game;
import com.example.dslist.projections.GameMinProjection;
import com.example.dslist.repositories.GameRepository;
import com.example.dslist.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<GameMinDTO> findAll(Pageable pageable) {
        Page<Game> result = gameRepository.findAll(pageable);

        return result
                .map(x -> gameMinMapper.gameToGameMinDTO(x));
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
