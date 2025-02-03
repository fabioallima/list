package com.example.dslist.services;

import com.example.dslist.dto.GameDTO;
import com.example.dslist.dto.GameMinDTO;
import com.example.dslist.dto.GamesListMinDTO;
import com.example.dslist.entities.Game;
import com.example.dslist.mappers.GameMapper;
import com.example.dslist.mappers.GameMinMapper;
import com.example.dslist.mappers.GamesListMinMapper;
import com.example.dslist.projections.GameMinProjection;
import com.example.dslist.repositories.GameRepository;
import com.example.dslist.services.exceptions.DatabaseException;
import com.example.dslist.services.exceptions.ResourceNotFoundException;
import com.example.dslist.tests.GameFactory;
import com.example.dslist.tests.GameListFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class GameServiceTests {

    @InjectMocks
    private GameService service;

    @Mock
    private GameRepository repository;

    @Mock
    private GameMinMapper gameMinMapper;

    @Mock
    private GameMapper gameMapper;

    @Mock
    private GamesListMinMapper gamesListMinMapper;

    private Game game;
    private GameMinDTO gameMinDTO;
    private GameDTO gameDTO;
    private GamesListMinDTO gamesListMinDTO;
    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private long existingListId;
    private long nonExistingListId;

    @BeforeEach
    void setUp() {
        game = GameFactory.createGame();
        gameMinDTO = GameFactory.createGameMinDTO();
        gameDTO = GameFactory.createGameDTO();
        gamesListMinDTO = GameListFactory.createGamesListMinDTO();
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        existingListId = 1L;
        nonExistingListId = 99L;
    }

    @Test
    public void findAllPaged_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 12);
        List<Game> games = List.of(game);
        Page<Game> page = new PageImpl<>(games);

        Mockito.when(repository.findAll(any(Pageable.class))).thenReturn(page);
        Mockito.when(gameMinMapper.gameToGameMinDTO(any(Game.class))).thenReturn(gameMinDTO);

        Page<GameMinDTO> result = service.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getTotalElements());
        GameMinDTO dtoResultado = result.getContent().get(0);
        Assertions.assertEquals(gameMinDTO.id(), dtoResultado.id());
        Assertions.assertEquals(gameMinDTO.title(), dtoResultado.title());
        Assertions.assertEquals(gameMinDTO.year(), dtoResultado.year());
        Assertions.assertEquals(gameMinDTO.imgUrl(), dtoResultado.imgUrl());
        Assertions.assertEquals(gameMinDTO.shortDescription(), dtoResultado.shortDescription());
        Mockito.verify(repository, times(1)).findAll(pageable);
    }

    @Test
    public void findById_ShouldReturnGameDTO_WhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(game));
        Mockito.when(gameMapper.gameToGameDTO(game)).thenReturn(gameDTO);

        GameDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.id());
        Mockito.verify(repository, times(1)).findById(existingId);
        Mockito.verify(gameMapper, times(1)).gameToGameDTO(game);
    }

    @Test
    public void findById_ShouldThrowResourceNotFoundException_WhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });

        Mockito.verify(repository, times(1)).findById(nonExistingId);
    }

    @Test
    public void findByList_ShouldReturnGamesListMinDTO_WhenListIdExists() {
        List<GameMinProjection> projections = List.of(mock(GameMinProjection.class));
        List<GamesListMinDTO> expectedList = List.of(gamesListMinDTO);

        Mockito.when(repository.searchByList(existingListId)).thenReturn(projections);
        Mockito.when(gamesListMinMapper.projectionToDto(any(GameMinProjection.class))).thenReturn(gamesListMinDTO);

        List<GamesListMinDTO> result = service.findByList(existingListId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedList.size(), result.size());
        Assertions.assertEquals(expectedList.get(0), result.get(0));
        Mockito.verify(repository).searchByList(existingListId);
        Mockito.verify(gamesListMinMapper, times(projections.size())).projectionToDto(any(GameMinProjection.class));
    }

    @Test
    public void findByList_ShouldReturnEmptyList_WhenListIdDoesNotExist() {
        Mockito.when(repository.searchByList(nonExistingListId)).thenReturn(List.of());

        List<GamesListMinDTO> result = service.findByList(nonExistingListId);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(repository).searchByList(nonExistingListId);
        Mockito.verify(gamesListMinMapper, never()).projectionToDto(any(GameMinProjection.class));
    }

    @Test
    public void findByListJPQL_ShouldReturnGamesListMinDTO_WhenListIdExists() {
        List<GamesListMinDTO> expectedList = List.of(gamesListMinDTO);

        Mockito.when(repository.searchByListJPQL(existingListId)).thenReturn(expectedList);

        List<GamesListMinDTO> result = service.findByListJPQL(existingListId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedList.size(), result.size());
        Assertions.assertEquals(expectedList.get(0), result.get(0));
        Mockito.verify(repository).searchByListJPQL(existingListId);
    }

    @Test
    public void findByListJPQL_ShouldReturnEmptyList_WhenListIdDoesNotExist() {
        Mockito.when(repository.searchByListJPQL(nonExistingListId)).thenReturn(List.of());

        List<GamesListMinDTO> result = service.findByListJPQL(nonExistingListId);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(repository).searchByListJPQL(nonExistingListId);
    }

    @Test
    void insert_ShouldReturnGameDTO_WhenSuccessful() {
        Mockito.when(gameMapper.gameDTOToGame(gameDTO)).thenReturn(game);
        Mockito.when(repository.save(game)).thenReturn(game);
        Mockito.when(gameMapper.gameToGameDTO(game)).thenReturn(gameDTO);

        GameDTO result = service.insert(gameDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.id());
        Assertions.assertEquals(gameDTO.title(), result.title());
        Mockito.verify(gameMapper).gameDTOToGame(gameDTO);
        Mockito.verify(repository).save(game);
        Mockito.verify(gameMapper).gameToGameDTO(game);
    }

    @Test
    void update_ShouldReturn_UpdatedGameDTO() {
        Mockito.when(repository.getReferenceById(existingId)).thenReturn(game);
        Mockito.doNothing().when(gameMapper).updateGameFromDTO(gameDTO, game);
        Mockito.when(repository.save(any(Game.class))).thenReturn(game);
        Mockito.when(gameMapper.gameToGameDTO(game)).thenReturn(gameDTO);

        // Act
        GameDTO result = service.update(existingId, gameDTO);

        // Assert
        Assertions.assertNotNull(result);
        Mockito.verify(repository).getReferenceById(existingId);
        Mockito.verify(gameMapper).updateGameFromDTO(gameDTO, game);
        Mockito.verify(repository).save(game);
        Mockito.verify(gameMapper).gameToGameDTO(any(Game.class));
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenGameDoesNotExist() {

        Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(nonExistingId, gameDTO));
        Mockito.verify(repository).getReferenceById(nonExistingId);
        Mockito.verifyNoInteractions(gameMapper);
    }

    @Test
    void delete_ShouldDoNothing_WhenIdExists() {
        Mockito.doNothing().when(repository).deleteById(existingId);

        Assertions.assertDoesNotThrow(() -> service.delete(existingId));

        Mockito.verify(repository, times(1)).deleteById(existingId);
    }

    @Test
    void delete_ShouldThrowDatabaseException_WhenDependentId() {
        doThrow(DataIntegrityViolationException.class)
                .when(repository).deleteById(dependentId);

        assertThrows(DatabaseException.class, () -> service.delete(dependentId));

        verify(repository, times(1)).deleteById(dependentId);
    }
}

