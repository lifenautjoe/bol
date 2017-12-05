package com.lifenautjoe.bol.domain;

public class GamePlayOutcome {

    private boolean gameFinished;

    private User nextTurnHolder;

    private User winner;

    public User getNextTurnHolder() {
        return nextTurnHolder;
    }

    public void setNextTurnHolder(User nextTurnHolder) {
        this.nextTurnHolder = nextTurnHolder;
    }

    public boolean isGameFinished() {
        return gameFinished;
    }

    public void setGameFinished(boolean gameFinished) {
        this.gameFinished = gameFinished;
    }

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }
}
