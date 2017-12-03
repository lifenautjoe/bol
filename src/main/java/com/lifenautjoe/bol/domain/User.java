package com.lifenautjoe.bol.domain;

public class User {
    private String name;
    private Game currentGame;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(Game currentGame) {
        this.currentGame = currentGame;
    }

    public boolean hasGame() {
        return this.currentGame == null;
    }

    public boolean isPlayingGame() {
        return currentGame != null;
    }
}
