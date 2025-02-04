package com.example.dslist.controllers;

import com.example.dslist.dto.GameListDTO;
import com.example.dslist.dto.GamesListMinDTO;
import com.example.dslist.dto.ReplacementDTO;
import com.example.dslist.services.GameListService;
import com.example.dslist.services.GameService;
import com.example.dslist.tests.GameListFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = GameListController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class GameListControllerTests {
    private static final String URI = "/lists";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameListService gameListService;

    @MockitoBean
    private GameService gameService;

    @Autowired
    private ObjectMapper objectMapper;

    private Long existingListId;
    private Long nonExistingListId;
    private GameListDTO gameListDTO;
    private GamesListMinDTO gamesListMinDTO;
    private List<GameListDTO> gameListDTOList;
    private List<GamesListMinDTO> gamesListMinDTOList;

    @BeforeEach
    void setUp() throws Exception {
        existingListId = 1L;
        nonExistingListId = 2L;

        gameListDTO = GameListFactory.createGameListDTO();
        gamesListMinDTO = GameListFactory.createGamesListMinDTO();
        gameListDTOList = List.of(gameListDTO);
        gamesListMinDTOList = List.of(gamesListMinDTO);

        when(gameListService.findAll()).thenReturn(gameListDTOList);
        when(gameService.findByList(existingListId)).thenReturn(gamesListMinDTOList);
        when(gameService.findByList(nonExistingListId)).thenReturn(List.of());

        doNothing().when(gameListService).move(eq(existingListId), any(Integer.class), any(Integer.class));
    }

    @Test
    public void findAllShouldReturnList() throws Exception {
        ResultActions result =
                mockMvc.perform(get(URI)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0].id").exists());
        result.andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    public void findByListShouldReturnListWhenListIdExists() throws Exception {
        ResultActions result =
                mockMvc.perform(get(URI + "/{listId}/games", existingListId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0].id").exists());
        result.andExpect(jsonPath("$[0].title").exists());
    }

    @Test
    public void findByListShouldReturnEmptyListWhenListIdDoesNotExist() throws Exception {
        ResultActions result =
                mockMvc.perform(get(URI + "/{listId}/games", nonExistingListId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$").isArray());
        result.andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void moveShouldReturnNoContentWhenSuccessful() throws Exception {
        ReplacementDTO replacementDTO = new ReplacementDTO(0, 1);
        String jsonBody = objectMapper.writeValueAsString(replacementDTO);

        ResultActions result =
                mockMvc.perform(post(URI + "/{listId}/replacement", existingListId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        verify(gameListService, times(1)).move(eq(existingListId), eq(0), eq(1));
    }
}
