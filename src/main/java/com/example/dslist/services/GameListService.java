package com.example.dslist.services;

import com.example.dslist.Mappers.GameListMapper;
import com.example.dslist.Mappers.GameMapper;
import com.example.dslist.dto.GameDTO;
import com.example.dslist.dto.GameListDTO;
import com.example.dslist.dto.GameMinDTO;
import com.example.dslist.entities.Game;
import com.example.dslist.entities.GameList;
import com.example.dslist.projections.GameMinProjection;
import com.example.dslist.repositories.GameListRepository;
import com.example.dslist.repositories.GameRepository;
import com.example.dslist.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GameListService {

    @Autowired
    private GameListRepository gameListRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameListMapper gameListMapper;

    @Transactional(readOnly = true)
    public List<GameListDTO> findAll() {
        List<GameList> result = gameListRepository.findAll();
        return result.stream()
                .map(x -> gameListMapper.gameListToGameListDTO(x))
                .toList();
    }

    @Transactional
    public void move(Long listId, int sourceIndex, int destinationIndex){
        List<GameMinProjection> list = gameRepository.searchByList(listId);

        GameMinProjection obj = list.remove(sourceIndex);
        list.add(destinationIndex, obj);

        //int min = sourceIndex < destinationIndex ? sourceIndex : destinationIndex;
        //int max = sourceIndex < destinationIndex ? destinationIndex : sourceIndex;

        int min = Math.min(sourceIndex, destinationIndex);
        int max = Math.max(sourceIndex, destinationIndex);

        for(int i = min; i<= max; i++){
            gameListRepository.updateBelongingPosition(listId, list.get(i).getId(), i);
        }
    }

}
