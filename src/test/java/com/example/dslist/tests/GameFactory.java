package com.example.dslist.tests;

import com.example.dslist.dto.GameDTO;
import com.example.dslist.dto.GameMinDTO;
import com.example.dslist.dto.GamesListMinDTO;
import com.example.dslist.entities.Game;
import com.example.dslist.entities.GameList;

public class GameFactory {
    public static Game createGame() {
        return new Game(1L, "Jogo Teste", 2022, "Gênero Teste", "Plataforma Teste", 4.5, "Descrição Curta Teste", "Descrição Longa Teste", "http://teste.com/imagem.jpg");
    }



    public static GameMinDTO createGameMinDTO() {
        return new GameMinDTO(1L, "Jogo Teste", 2022, "http://teste.com/imagem.jpg", "Descrição Curta Teste");
    }

    public static GameDTO createGameDTO() {
        return new GameDTO(1L, "Test Game", 2023, "Action", "PC, PS5", 9.5, "http://example.com/image.jpg", "A short description", "A longer description of the game");
    }


}
