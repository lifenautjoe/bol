package com.lifenautjoe.bol.controllers.games.responses;

import com.lifenautjoe.bol.domain.Game;

public class GameResponse {
    private String gameName;
    private boolean isFull;

    public GameResponse(Game game) {
        gameName = game.getName();
        isFull = game.isFull();
    }

    public String getGameName() {
        return gameName;
    }

    public boolean isFull() {
        return isFull;
    }
}
