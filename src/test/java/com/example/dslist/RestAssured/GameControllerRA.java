package com.example.dslist.RestAssured;

import com.example.dslist.tests.TokenUtilRA;
import io.restassured.http.ContentType;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class GameControllerRA {

    private String clientUsername, clientPassword, adminUsername, adminPassword;
    private String clientToken, adminToken, invalidToken;
    private Integer existingGameId, nonExistingGameId;
    private Map<String, Object> postGameInstance;

    @BeforeEach
    public void setUp() {
        baseURI = "http://localhost:8080";

        existingGameId = 2;

        clientUsername = "alex@gmail.com";
        clientPassword = "123456";

        adminUsername = "maria@gmail.com";
        adminPassword = "123456";

        clientToken = TokenUtilRA.obtainAccessToken(clientUsername, clientPassword);
        adminToken = TokenUtilRA.obtainAccessToken(adminUsername, adminPassword);
        invalidToken = adminToken + "xpto";

        postGameInstance = new HashMap<>();
        postGameInstance.put("title", "Quantum Nexus");
        postGameInstance.put("year", 2025);
        postGameInstance.put("genre", "Sci-Fi RPG");
        postGameInstance.put("platforms", "PC, PlayStation 6, Xbox Series Z");
        postGameInstance.put("score", 9.5);
        postGameInstance.put("imgUrl", "https://example.com/quantum-nexus-cover.jpg");
        postGameInstance.put("shortDescription", "Embark on an interdimensional journey in this groundbreaking sci-fi RPG set in a universe where reality bends to your will.");
        postGameInstance.put("longDescription", "Quantum Nexus is a revolutionary sci-fi RPG that pushes the boundaries of gaming technology and storytelling. Set in the year 3045, players navigate a multiverse where quantum mechanics governs reality. As the Nexus Keeper, you must master the ability to manipulate quantum states, altering the fabric of existence itself. Explore diverse alien worlds, each with its own unique physics and challenges. Engage in strategic combat that combines traditional RPG elements with quantum mechanics, allowing for mind-bending tactics. Your choices ripple across dimensions, affecting parallel realities and shaping the fate of the multiverse. With its stunning visuals, complex narrative, and innovative gameplay mechanics, Quantum Nexus offers an unparalleled gaming experience that will redefine the genre.");

    }

    @Test
    public void findAll_ShouldReturnGamesPagedByTitle() {
        given()
                .get("/games?page=0&size=20&sort=title")
                .then()
                .statusCode(200)
                .body("_embedded.gameMinDTOList.id", hasItems(9,5,7))
                .body("_embedded.gameMinDTOList.title", hasItems("Cuphead","Ghost of Tsushima", "Hollow Knight"))
                .body("_embedded.gameMinDTOList[2].year", is(2017))
                .body("_embedded.gameMinDTOList.find { it.id == 6 }.year", is(1990))
                .body("_links.self.href", equalTo("http://localhost:8080/games?page=0&size=20&sort=title,asc"))
                .body("page.size", is(20));
    }

    @Test
    public void findById_ShouldReturnGame_WhenIdExists() {
        given()
                .get("/games/{id}", existingGameId)
        .then()
                .statusCode(200)
                .body("id", is(existingGameId))
                .body("title", equalTo("Red Dead Redemption 2"))
                .body("year", is(2018))
                .body("platforms", equalTo("XBox, Playstation, PC"));
    }

    @Test
    public void insert_ShouldReturnGameCreated_WhenAuthenticated() {
        JSONObject newGame = new JSONObject(postGameInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newGame)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
        .when()
                .post("/games")
        .then()
                .statusCode(201)
                .body("title", equalTo("Quantum Nexus"));
    }

    @Test
    public void insert_ShouldReturnUnprocessableEntity_WhenAuthenticated() {
        postGameInstance.put("title", "ab");
        JSONObject newGame = new JSONObject(postGameInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newGame)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
            .when()
                .post("/games")
            .then()
                .statusCode(422)
                .body("errors[0].fieldName", equalTo("title"))
                .body("errors[0].message", equalTo("Deve ter entre 5 e 80 caracteres"))
                .body("error", equalTo("Validation exception"));
    }

    @Test
    public void insert_ShouldForbidden_WhenClientLogged() {
        JSONObject newGame = new JSONObject(postGameInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .body(newGame)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
            .when()
                .post("/games")
            .then()
                .statusCode(403)
                .body("error", equalTo("Forbidden"));
    }

    @Test
    public void insert_ShouldUnauthorized_WhenInvalidToken() {
        JSONObject newGame = new JSONObject(postGameInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .body(newGame)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
            .when()
                .post("/games")
            .then()
                .statusCode(401);
    }

    @Test
    public void delete_ShouldReturnNoContent_WhenIdExistAndAuthenticated() {
        existingGameId = 11;

        given()
                .header("Authorization", "Bearer " + adminToken)
            .when()
                .delete("/games/{id}", existingGameId)
            .then()
                .statusCode(204);
    }

    @Test
    public void update_ShouldReturnGameupdated_WhenAuthenticated() {
        JSONObject newGame = new JSONObject(postGameInstance);
        existingGameId = 1;

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newGame)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
            .when()
                .put("/games/{id}", existingGameId)
            .then()
                .statusCode(200)
                .body("title", equalTo("Quantum Nexus"));
    }
}
