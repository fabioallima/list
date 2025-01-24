package com.example.dslist.tests;

import com.example.dslist.dto.GameMinDTO;
import com.example.dslist.entities.Game;

public class Factory {
    public static Game createGame() {
        return new Game(1L, "Jogo Teste", 2022, "Gênero Teste", "Plataforma Teste", 4.5, "Descrição Curta Teste", "Descrição Longa Teste", "http://teste.com/imagem.jpg");
    }

    public static GameMinDTO createGameMinDTO() {
        return new GameMinDTO(1L, "Jogo Teste", 2022, "http://teste.com/imagem.jpg", "Descrição Curta Teste");
    }
}
