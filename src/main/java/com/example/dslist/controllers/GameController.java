package com.example.dslist.controllers;

import com.example.dslist.dto.GameDTO;
import com.example.dslist.dto.GameMinDTO;
import com.example.dslist.dto.UserDTO;
import com.example.dslist.services.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


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

    @PreAuthorize("hasAnyRole('ROLE_OPERATOR', 'ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<GameDTO> insert(@Valid @RequestBody GameDTO dto){
        GameDTO gameDTO = gameService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(gameDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_OPERATOR', 'ROLE_ADMIN')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<GameDTO> update(@PathVariable Long id, @RequestBody @Valid GameDTO dto) {
        GameDTO gameDTO = gameService.update(id, dto);

        return ResponseEntity.ok().body(gameDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_OPERATOR', 'ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gameService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
