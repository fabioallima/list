package com.example.dslist.services;

import com.example.dslist.dto.*;
import com.example.dslist.entities.User;
import com.example.dslist.mappers.GameMapper;
import com.example.dslist.mappers.GameMinMapper;
import com.example.dslist.mappers.GamesListMinMapper;
import com.example.dslist.entities.Game;
import com.example.dslist.projections.GameMinProjection;
import com.example.dslist.repositories.GameRepository;
import com.example.dslist.services.exceptions.DatabaseException;
import com.example.dslist.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    @PreAuthorize("hasAnyRole('ROLE_OPERATOR', 'ROLE_ADMIN')")
    @Transactional
    public GameDTO insert(GameDTO dto) {
        Game entity = gameMapper.gameDTOToGame(dto);
        entity = gameRepository.save(entity);

        return gameMapper.gameToGameDTO(entity);
    }
    
    @Transactional
    public GameDTO update(Long id, GameDTO dto) {
        try {
            Game entity = gameRepository.getReferenceById(id);
            gameMapper.updateGameFromDTO(dto, entity);
            entity = gameRepository.save(entity);
            return gameMapper.gameToGameDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found: " + id);
        }
    }

    public void delete(Long id) {
        try {
            gameRepository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DatabaseException("Integraity violation");
        }
    }
}
