package com.example.dslist.services;

import com.example.dslist.dto.GameMinDTO;
import com.example.dslist.entities.Game;
import com.example.dslist.mappers.GameMinMapper;
import com.example.dslist.repositories.GameRepository;
import com.example.dslist.tests.Factory;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    private Game game;
    private GameMinDTO gameMinDTO;
    private Page<Game> page;

    @BeforeEach
    void setUp() {
        game = Factory.createGame();
        gameMinDTO = Factory.createGameMinDTO();

        List<Game> games = List.of(game);
        page = new PageImpl<>(games);

        when(repository.findAll(any(Pageable.class))).thenReturn(page);
        when(gameMinMapper.gameToGameMinDTO(any(Game.class))).thenReturn(gameMinDTO);
    }

    @Test
    public void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 12);
        Page<GameMinDTO> result = service.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        GameMinDTO dtoResultado = result.getContent().get(0);
        assertEquals(gameMinDTO.id(), dtoResultado.id());
        assertEquals(gameMinDTO.title(), dtoResultado.title());
        assertEquals(gameMinDTO.year(), dtoResultado.year());
        assertEquals(gameMinDTO.imgUrl(), dtoResultado.imgUrl());
        assertEquals(gameMinDTO.shortDescription(), dtoResultado.shortDescription());
        Mockito.verify(repository, times(1)).findAll(pageable);

    }
}
