package com.example.dslist.repositories;

import com.example.dslist.dto.GameMinDTO;
import com.example.dslist.dto.GamesListMinDTO;
import com.example.dslist.entities.Game;
import com.example.dslist.projections.GameMinProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    @Query(nativeQuery = true, value = """
            SELECT tb_game.id, tb_game.title, tb_game.game_year AS "year", tb_game.img_url AS imgUrl,
            tb_game.short_description AS shortDescription, tb_belonging.position
            FROM tb_game
            INNER JOIN tb_belonging ON tb_game.id = tb_belonging.game_id
            WHERE tb_belonging.list_id = :listId
            ORDER BY tb_belonging.position
            """)
    List<GameMinProjection> searchByList(Long listId);

    @Query("""
                SELECT NEW com.example.dslist.dto.GamesListMinDTO(
                    g.id,
                    g.title,
                    g.year,
                    g.imgUrl,
                    g.shortDescription,
                    b.position
                )
                FROM Game g
                INNER JOIN Belonging b ON g = b.id.game
                WHERE b.id.list.id = :listId
                ORDER BY b.position
            """)
    List<GamesListMinDTO> searchByListJPQL(Long listId);
}
