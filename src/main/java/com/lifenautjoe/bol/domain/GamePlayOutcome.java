package com.lifenautjoe.bol.domain;

import java.util.List;

public class GamePlayOutcome {

    private boolean gameFinished;
    // Make sure they are copies!
    private String nextTurnHolderUserName;
    private String winnerUserName;
    private List<GameSlot> slots;

    public String getNextTurnHolderUserName() {
        return nextTurnHolderUserName;
    }

    public void setNextTurnHolderUserName(String nextTurnHolderUserName) {
        this.nextTurnHolderUserName = nextTurnHolderUserName;
    }

    public String getWinnerUserName() {
        return winnerUserName;
    }

    public void setWinnerUserName(String winnerUserName) {
        this.winnerUserName = winnerUserName;
    }

    public boolean isGameFinished() {
        return gameFinished;
    }

    public void setGameFinished(boolean gameFinished) {
        this.gameFinished = gameFinished;
    }

    public List<GameSlot> getSlots() {
        return slots;
    }

    public void setSlots(List<GameSlot> slots) {
        this.slots = slots;
    }
}
