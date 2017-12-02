package com.lifenautjoe.bol.domain;

import java.util.LinkedList;
import java.util.List;

public class GameSlot {
    private final int id;
    private final User owner;
    private LinkedList<GameSlotStone> stones;

    public GameSlot(int id, User owner, LinkedList<GameSlotStone> stones) {
        this.id = id;
        this.owner = owner;
        this.stones = stones;
    }

    public int getId() {
        return id;
    }

    public boolean isEmpty() {
        return stones == null || stones.isEmpty();
    }

    public void dropStone(GameSlotStone stone) {
        this.stones.add(stone);
    }

    public void dropStones(List<GameSlotStone> stones) {
        for (GameSlotStone stone : stones) {
            this.dropStone(stone);
        }
    }

    public LinkedList<GameSlotStone> pickStones() {
        // Return clone
        LinkedList<GameSlotStone> stonesClone = (LinkedList<GameSlotStone>) stones.clone();
        stones.clear();
        return stonesClone;
    }

    public boolean belongsToUser(User user) {
        return user == this.owner;
    }
}
