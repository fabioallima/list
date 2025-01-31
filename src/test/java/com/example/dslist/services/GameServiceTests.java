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
import com.example.dslist.services.exceptions.ResourceNotFoundException;
import com.example.dslist.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
    private long existingListId;
    private long nonExistingListId;

    @BeforeEach
    void setUp() {
        game = Factory.createGame();
        gameMinDTO = Factory.createGameMinDTO();
        gameDTO = Factory.createGameDTO();
        gamesListMinDTO = Factory.createGamesListMinDTO();
        existingId = 1L;
        nonExistingId = 2L;
        existingListId = 1L;
        nonExistingListId = 99L;
    }

    @Test
    public void findAllPagedShouldReturnPage() {
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
    public void findByIdShouldReturnGameDTOWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(game));
        Mockito.when(gameMapper.gameToGameDTO(game)).thenReturn(gameDTO);

        GameDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.id());
        Mockito.verify(repository, times(1)).findById(existingId);
        Mockito.verify(gameMapper, times(1)).gameToGameDTO(game);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });

        Mockito.verify(repository, times(1)).findById(nonExistingId);
    }

    /*@Test
    public void findByListReturnGamesListMinDTOWhenListIdExists(){
        List<GamesListMinDTO> list = List.of(gamesListMinDTO);

        Mockito.when(repository.searchByList(existingListId)).thenReturn(list);
        Mockito.when(gamesListMinMapper.projectionToDto(any(GameMinProjection.class))).thenReturn(gamesListMinDTO);

        List<GamesListMinDTO> result = service.findByList(existingListId);
        Assertions.assertNotNull(result);
    }*/

    public void findByListShouldReturnGamesListMinDTOWhenListIdExists() {
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
    public void findByListShouldReturnEmptyListWhenListIdDoesNotExist() {
        Mockito.when(repository.searchByList(nonExistingListId)).thenReturn(List.of());

        List<GamesListMinDTO> result = service.findByList(nonExistingListId);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(repository).searchByList(nonExistingListId);
        Mockito.verify(gamesListMinMapper, never()).projectionToDto(any(GameMinProjection.class));
    }

    @Test
    public void findByListJPQLShouldReturnGamesListMinDTOWhenListIdExists() {
        List<GamesListMinDTO> expectedList = List.of(gamesListMinDTO);

        Mockito.when(repository.searchByListJPQL(existingListId)).thenReturn(expectedList);

        List<GamesListMinDTO> result = service.findByListJPQL(existingListId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedList.size(), result.size());
        Assertions.assertEquals(expectedList.get(0), result.get(0));
        Mockito.verify(repository).searchByListJPQL(existingListId);
    }

    @Test
    public void findByListJPQLShouldReturnEmptyListWhenListIdDoesNotExist() {
        Mockito.when(repository.searchByListJPQL(nonExistingListId)).thenReturn(List.of());

        List<GamesListMinDTO> result = service.findByListJPQL(nonExistingListId);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(repository).searchByListJPQL(nonExistingListId);
    }
}

