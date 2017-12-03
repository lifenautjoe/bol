package com.lifenautjoe.bol.services;

import com.lifenautjoe.bol.domain.Game;
import com.lifenautjoe.bol.domain.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GamesManagerService {

    private GameFactoryService gameFactoryService;

    private List<Game> games;

    public GamesManagerService(GameFactoryService gameFactoryService) {
        this.gameFactoryService = gameFactoryService;
        this.games = Collections.synchronizedList(new ArrayList<Game>());
    }

    public List<Game> getAll() {
        List<Game> listClone = new ArrayList<Game>(games);
        return listClone;
    }
}
