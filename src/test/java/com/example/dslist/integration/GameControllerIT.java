package com.example.dslist.integration;

import com.example.dslist.dto.GameDTO;
import com.example.dslist.tests.GameFactory;
import com.example.dslist.tests.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GameControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TokenUtil tokenUtil;

	private Long existingId;
	private Long nonExistingId;
	private Long countTotalGames;

	private String username, password, bearerToken;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalGames = 10L;

		username = "maria@gmail.com";
		password = "123456";

		bearerToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
	}

	@Test
	public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
		ResultActions result = mockMvc
				.perform(get("/games?page=0&size=12&sort=title,asc").accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.page.totalElements").value(countTotalGames));
		result.andExpect(jsonPath("$._embedded.gameMinDTOList").exists());
		result.andExpect(jsonPath("$._embedded.gameMinDTOList[0].title").value("Cuphead"));
		result.andExpect(jsonPath("$._embedded.gameMinDTOList[1].title").value("Ghost of Tsushima"));
		result.andExpect(jsonPath("$._embedded.gameMinDTOList[2].title").value("Hollow Knight"));


		JsonPath.read(result.andReturn().getResponse().getContentAsString(), "$.page.totalElements");
	}

	@Test
	public void updateShouldReturnGameDTOWhenIdExists() throws Exception {

		GameDTO gameDTO = GameFactory.createGameDTO();
		String jsonBody = objectMapper.writeValueAsString(gameDTO);

		String expectedTitle = gameDTO.title();
		String expectedShortDescription = gameDTO.shortDescription();

		ResultActions result = mockMvc
				.perform(put("/games/{id}", existingId)
						.header("Authorization", "Bearer " + bearerToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").value(existingId));
		result.andExpect(jsonPath("$.title").value(expectedTitle));
		result.andExpect(jsonPath("$.shortDescription").value(expectedShortDescription));
	}

	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

		GameDTO gameDTO = GameFactory.createGameDTO();
		String jsonBody = objectMapper.writeValueAsString(gameDTO);

		ResultActions result = mockMvc.perform(put("/games/{id}", nonExistingId)
				.header("Authorization", "Bearer " + bearerToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}
}
