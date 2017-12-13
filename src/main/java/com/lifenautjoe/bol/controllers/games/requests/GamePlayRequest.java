package com.lifenautjoe.bol.controllers.games.requests;

public class GamePlayRequest {
    private int slotId;
    private String gameName;

    public GamePlayRequest() {

    }

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
