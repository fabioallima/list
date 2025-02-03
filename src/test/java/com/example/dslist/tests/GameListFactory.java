package com.example.dslist.tests;

import com.example.dslist.dto.GameListDTO;
import com.example.dslist.dto.GamesListMinDTO;
import com.example.dslist.entities.GameList;

public class GameListFactory {

    public static GameList createGameList(){
        return new GameList(1L, "Test List");
    }

    public static GamesListMinDTO createGamesListMinDTO() {
        return new GamesListMinDTO(1L, "Test Game", 2023, "http://teste.com/imagem.jpg", "Short Description", 1);
    }

    public static GameListDTO createGameListDTO(){
        return new GameListDTO(1L, "Test List");
    }
}
