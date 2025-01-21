package com.example.dslist.services;

import com.example.dslist.Mappers.GameMapper;
import com.example.dslist.dto.GameDTO;
import com.example.dslist.dto.GameMinDTO;
import com.example.dslist.entities.Game;
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

    @Transactional(readOnly = true)
    public List<GameMinDTO> findAll() {
        List<Game> result = gameRepository.findAll();
        return result.stream()
                .map(x -> new GameMinDTO(x))
                .toList();
    }

    @Transactional(readOnly = true)
    public GameDTO findById(Long id){
        Game result = gameRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Game not found"));
        return gameMapper.gameToGameDTO(result);
    }

}
