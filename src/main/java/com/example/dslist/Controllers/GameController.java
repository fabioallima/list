package com.example.dslist.Controllers;

import com.example.dslist.dto.GameDTO;
import com.example.dslist.dto.GameMinDTO;
import com.example.dslist.services.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;



@RestController
@RequestMapping(value = "/games")
@Tag(name = "Games", description = "API para gerenciamento de jogos")
public class GameController {
    @Autowired
    private GameService gameService;

    @Autowired
    private PagedResourcesAssembler<GameMinDTO> pagedResourcesAssembler;

    @GetMapping
    @Operation(summary = "Listar todos os jogos",
            description = "Retorna uma lista paginada de jogos com informações resumidas")
    @ApiResponse(responseCode = "200", description = "Operação bem-sucedida",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)))
    public ResponseEntity<PagedModel<EntityModel<GameMinDTO>>> findAll(Pageable pageable) {
        Page<GameMinDTO> result = gameService.findAll(pageable);
        PagedModel<EntityModel<GameMinDTO>> pagedModel = pagedResourcesAssembler.toModel(result);
        return ResponseEntity.ok().body(pagedModel);
    }

    /*public ResponseEntity<Page<GameMinDTO>> findAll(Pageable pageable) {
        Page<GameMinDTO> result = gameService.findAll(pageable);

        return ResponseEntity.ok().body(result);
    }*/


    @GetMapping(value = "/{id}")
    @Operation(summary = "Buscar jogo por ID",
            description = "Retorna as informações detalhadas de um jogo específico")
    @ApiResponse(responseCode = "200", description = "Jogo encontrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GameDTO.class)))
    @ApiResponse(responseCode = "404", description = "Jogo não encontrado")
    public GameDTO findById(
            @Parameter(description = "ID do jogo", required = true) @PathVariable Long id) {
        GameDTO result = gameService.findById(id);
        return result;
    }
}
