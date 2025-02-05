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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
//@IntegrationTest
public class GameControllerIT {

	private static final String URI = "/games";
	private static final String username = "maria@gmail.com";
	private static final String password = "123456";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TokenUtil tokenUtil;

	private Long existingId;
	private Long nonExistingId;
	private Long countTotalGames;

	private String bearerToken;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalGames = 10L;

		bearerToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
	}

	@Test
	public void findAll_ShouldReturnSortedPage_WhenSortByTitle() throws Exception {
		ResultActions result = mockMvc
				.perform(MockMvcRequestBuilders.get(URI+"?page=0&size=12&sort=title,asc").accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.page.totalElements").value(countTotalGames));
		result.andExpect(jsonPath("$._embedded.gameMinDTOList").exists());
		result.andExpect(jsonPath("$._embedded.gameMinDTOList[0].title").value("Cuphead"));
		result.andExpect(jsonPath("$._embedded.gameMinDTOList[1].title").value("Ghost of Tsushima"));
		result.andExpect(jsonPath("$._embedded.gameMinDTOList[2].title").value("Hollow Knight"));


		JsonPath.read(result.andReturn().getResponse().getContentAsString(), "$.page.totalElements");
	}

	@Test
	public void findById_ShouldReturnGameDTO_WhenIdExists() throws Exception {
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(URI + "/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.title").exists());
		result.andExpect(jsonPath("$.shortDescription").exists());
	}

	@Test
	public void findById_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(URI + "/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}

	@Test
	public void insert_ShouldReturnCreatedAndGameDTO_WhenDataIsValid() throws Exception {
		GameDTO gameDTO = GameFactory.createGameDTO();
		String jsonBody = objectMapper.writeValueAsString(gameDTO);

		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(URI)
				.header("Authorization", "Bearer " + bearerToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.title").value(gameDTO.title()));
		result.andExpect(jsonPath("$.shortDescription").value(gameDTO.shortDescription()));
	}

	@Test
	public void insert_ShouldReturnUnauthorized_WhenNoToken() throws Exception {
		GameDTO gameDTO = GameFactory.createGameDTO();
		String jsonBody = objectMapper.writeValueAsString(gameDTO);

		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(URI)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isUnauthorized());
	}

	@Test
	public void update_ShouldReturnGameDTO_WhenIdExists() throws Exception {

		GameDTO gameDTO = GameFactory.createGameDTO();
		String jsonBody = objectMapper.writeValueAsString(gameDTO);

		String expectedTitle = gameDTO.title();
		String expectedShortDescription = gameDTO.shortDescription();

		ResultActions result = mockMvc
				.perform(MockMvcRequestBuilders.put(URI + "/{id}", existingId)
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
	public void update_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {

		GameDTO gameDTO = GameFactory.createGameDTO();
		String jsonBody = objectMapper.writeValueAsString(gameDTO);

		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(URI+"/{id}", nonExistingId)
				.header("Authorization", "Bearer " + bearerToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}

	@Test
	public void delete_ShouldReturnNoContent_WhenIdExists() throws Exception {
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete(URI + "/{id}", existingId)
				.header("Authorization", "Bearer " + bearerToken)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNoContent());
	}

	@Test
	public void delete_ShouldReturnUnauthorized_WhenNoToken() throws Exception {
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete(URI + "/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isUnauthorized());
	}

}
