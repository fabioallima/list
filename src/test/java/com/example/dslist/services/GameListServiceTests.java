package com.example.dslist.services;

import com.example.dslist.dto.GameListDTO;
import com.example.dslist.entities.GameList;
import com.example.dslist.mappers.GameListMapper;
import com.example.dslist.projections.GameMinProjection;
import com.example.dslist.repositories.GameListRepository;
import com.example.dslist.repositories.GameRepository;
import com.example.dslist.tests.GameListFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameListServiceTests {

    @InjectMocks
    private GameListService gameListService;

    @Mock
    private GameListRepository gameListRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameListMapper gameListMapper;

    private GameList gameList1;
    private GameList gameList2;
    private GameListDTO gameListDTO1;
    private GameListDTO gameListDTO2;

    @BeforeEach
    void setUp() {
        gameList1 = GameListFactory.createGameList();
        gameList2 = GameListFactory.createGameList();
        gameListDTO1 = GameListFactory.createGameListDTO();
        gameListDTO2 = GameListFactory.createGameListDTO();
    }

    @Test
    void findAll_ShouldReturn_ListOfGameListDTO() {
        // Arrange
        when(gameListRepository.findAll()).thenReturn(List.of(gameList1, gameList2));
        when(gameListMapper.gameListToGameListDTO(gameList1)).thenReturn(gameListDTO1);
        when(gameListMapper.gameListToGameListDTO(gameList2)).thenReturn(gameListDTO2);

        // Act
        List<GameListDTO> result = gameListService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(gameListDTO1, result.get(0));
        assertEquals(gameListDTO2, result.get(1));
        verify(gameListRepository).findAll();
        verify(gameListMapper, times(2)).gameListToGameListDTO(any(GameList.class));
    }

    @Test
    void move_ShouldUpdate_PositionsCorrectly() {
        // Arrange
        Long listId = 1L;
        int sourceIndex = 0;
        int destinationIndex = 2;
        List<GameMinProjection> projections = mock(List.class);
        GameMinProjection projection = mock(GameMinProjection.class);

        when(gameRepository.searchByList(listId)).thenReturn(projections);
        when(projections.get(anyInt())).thenReturn(projection);

        // Act
        gameListService.move(listId, sourceIndex, destinationIndex);

        // Assert
        verify(gameRepository).searchByList(listId);
        verify(gameListRepository, times(3)).updateBelongingPosition(eq(listId), anyLong(), anyInt());
    }
}
