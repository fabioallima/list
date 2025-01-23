package com.example.dslist.Controllers;

import com.example.dslist.dto.GameListDTO;
import com.example.dslist.dto.GamesListMinDTO;
import com.example.dslist.dto.ReplacementDTO;
import com.example.dslist.services.GameListService;
import com.example.dslist.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/lists")
public class GameListController {
    @Autowired
    private GameListService gameListService;

    @Autowired
    private GameService gameService;

    @GetMapping
    public List<GameListDTO> findAll() {
        List<GameListDTO> result = gameListService.findAll();
        return result;
    }

    @GetMapping(value = "/{listId}/games")
    public List<GamesListMinDTO> findbyList(@PathVariable Long listId) {
        List<GamesListMinDTO> result = gameService.findByList(listId);
        return result;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    @PostMapping(value = "/{listId}/replacement")
    public void move(@PathVariable Long listId, @RequestBody ReplacementDTO body) {
        gameListService.move(listId, body.sourceIndex(), body.destinationIndex());
    }

}
