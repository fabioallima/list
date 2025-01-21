package com.example.dslist.Controllers;

import com.example.dslist.dto.GameMinDTO;
import com.example.dslist.services.GameServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/games")
public class GameController {
    @Autowired
    private GameServices gameServices;

    @GetMapping
    public List<GameMinDTO> findAll() {
        List<GameMinDTO> result = gameServices.findAll();
        return result;
    }


}
