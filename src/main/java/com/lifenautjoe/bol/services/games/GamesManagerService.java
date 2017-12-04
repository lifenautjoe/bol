package com.lifenautjoe.bol.services.games;

import com.lifenautjoe.bol.domain.Game;
import com.lifenautjoe.bol.services.games.exceptions.GameAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GamesManagerService {

    private GameFactoryService gameFactoryService;

    private Map<String, Game> games;

    @Autowired
    public GamesManagerService(GameFactoryService gameFactoryService) {
        this.gameFactoryService = gameFactoryService;
        this.games = Collections.synchronizedMap(new HashMap<String, Game>());
    }

    public Game getOrCreateGameWithName(String gameName) {
        Game game = getGameWithName(gameName);
        if (game == null) {
            game = createGameWithName(gameName);
        }
        return game;
    }

    public Game getGameWithName(String gameName) {
        return games.get(gameName);
    }

    public Game createGameWithName(String gameName) {
        if (hasGameWithName(gameName)) {
            throw new GameAlreadyExistsException();
        }
        Game game = gameFactoryService.makeGameWithName(gameName);
        games.put(game.getName(), game);
        return game;
    }

    public boolean hasGameWithName(String gameName) {
        return games.containsKey(gameName);
    }

    public Collection<Game> getAll() {
        return games.values();
    }
}
