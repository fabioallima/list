package com.example.dslist.repositories;

import com.example.dslist.dto.GamesListMinDTO;
import com.example.dslist.entities.Game;
import com.example.dslist.entities.GameList;
import com.example.dslist.projections.GameMinProjection;
import com.example.dslist.tests.GameFactory;
import com.example.dslist.tests.GameListFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

@DataJpaTest
public class GameRepositoryTests {

    @Autowired
    private GameRepository repository;

    private long exintingId;
    private long nonExistingId;
    private long countTotalGames;

    private GameList gameList;

    @BeforeEach
    void setUp() throws Exception {
        exintingId = 1L;
        nonExistingId = 1000L;
        countTotalGames = 10L;
    }

    @Test
    public void saveShouldPersistWithAutoincrementWhenIdIsNull() {

        Game game = GameFactory.createGame();
        game.setId(null);

        game = repository.save(game);
        Optional<Game> result = repository.findById(game.getId());

        Assertions.assertNotNull(game.getId());
        Assertions.assertEquals(countTotalGames + 1L, game.getId());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertSame(result.get(), game);
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        repository.deleteById(exintingId);
        Optional<Game> result = repository.findById(exintingId);
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void findByIdShouldReturnGameNotEmptyWhenIdExist() {
        Optional<Game> game = repository.findById(exintingId);
        Assertions.assertNotNull(game);
    }

    @Test
    public void findByIdShouldReturnGameEmptyWhenIdNotExist() {
        Optional<Game> game = repository.findById(nonExistingId);
        Assertions.assertEquals(Optional.empty(), game);
    }

    @Test
    void searchByListShouldReturnGameMinProjections() {
        gameList = GameListFactory.createGameList();
        List<GameMinProjection> result = repository.searchByList(gameList.getId());
        Assertions.assertNotNull(result);
    }

    @Test
    void searchByListJPQLShouldReturnGamesListMinDTOs() {
        gameList = GameListFactory.createGameList();
        List<GamesListMinDTO> result = repository.searchByListJPQL(gameList.getId());
        Assertions.assertNotNull(result);
    }
}
