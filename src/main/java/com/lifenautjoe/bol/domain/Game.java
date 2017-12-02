package com.lifenautjoe.bol.domain;

import com.google.common.collect.Iterables;

import java.util.*;

public class Game {

    private static final int BOARD_SLOTS = 14;
    private static final int STONES_PER_BOARD_SLOT = 6;
    private User userA;
    private User userB;
    private GameSlot userAStorageSlot;
    private GameSlot userBStorageSlot;

    // For quick find
    private Map<Integer, GameSlot> slots;

    // For making iterators
    private Collection<GameSlot> slotsCollection;

    public Game() {

    }

    public void startGame() {
        this.slots = makeSlots();
        this.slotsCollection = this.slots.values();
        this.userAStorageSlot = this.slots.get(BOARD_SLOTS / 2);
        this.userBStorageSlot = this.slots.get(BOARD_SLOTS);
    }

    public GamePlayOutcome playAtSlotIdForUser(int slotId, User user) {
        GameSlot slot = this.getSlotWithId(slotId);
        return this.playAtSlotForUser(slot, user);
    }

    public GamePlayOutcome playAtSlotForUser(GameSlot slot, User user) {

        GamePlayOutcome playOutcome = new GamePlayOutcome();

        Iterator<GameSlot> slotIterator = getIteratorAtSlot(slot);

        LinkedList<GameSlotStone> userStones = slot.pickStones();

        while (!userStones.isEmpty()) {

            GameSlot nextSlot = slotIterator.next();

            if (nextSlot.belongsToUser(user)) {
                if (userStones.size() == 1) {
                    // Last one
                    if (slotIsUserStorage(nextSlot)) {
                        // Drop it and extra turn!
                        GameSlotStone userStoneToAddToSlot = userStones.pop();
                        nextSlot.dropStone(userStoneToAddToSlot);

                        playOutcome.setUserHasAnotherTurn(true);
                    } else if (nextSlot.isEmpty()) {
                        // We take the stone to the storage and all of the ones across
                        GameSlot slotAcrossBoard = getSlotAcrossBoard(nextSlot);
                        List<GameSlotStone> slotAcrossBoardStones = slotAcrossBoard.pickStones();
                        GameSlot userStorageSlot = getStorageSlotForUser(user);
                        userStorageSlot.dropStones(slotAcrossBoardStones);
                    }
                } else {
                    // A simple drop
                    GameSlotStone userStoneToAddToSlot = userStones.pop();
                    nextSlot.dropStone(userStoneToAddToSlot);
                }
            }
        }

        return playOutcome;
    }

    public User getUserA() {
        return userA;
    }

    public void setUserA(User userA) {
        this.userA = userA;
    }

    public User getUserB() {
        return userB;
    }

    public void setUserB(User userB) {
        this.userB = userB;
    }

    private Iterator<GameSlot> getIteratorAtSlot(GameSlot slot) {
        Iterator<GameSlot> iterator = Iterables.cycle(Iterables.skip(slotsCollection, slot.getId())).iterator();
        return iterator;
    }

    private GameSlot getStorageSlotForUser(User user) {
        return user == userA ? userAStorageSlot : userBStorageSlot;
    }

    private GameSlot getSlotAcrossBoard(GameSlot slot) {
        int slotId = slot.getId();
        int acrossBoardSlotId = (BOARD_SLOTS - slotId) + 1;
        return getSlotWithId(acrossBoardSlotId);
    }

    private GameSlot getSlotWithId(int slotId) {
        return slots.get(slotId);
    }

    private Map<Integer, GameSlot> makeSlots() {

        int slotsPerUser = BOARD_SLOTS / 2;

        Map<Integer, GameSlot> slots = new HashMap<>();

        for (int slotId = 1; slotId < BOARD_SLOTS; slotId++) {

            User slotOwner = null;

            if (slotId > slotsPerUser) {
                slotOwner = userB;
            } else {
                slotOwner = userA;
            }

            LinkedList<GameSlotStone> slotStones = new LinkedList<>();

            for (int stoneId = 1; stoneId < STONES_PER_BOARD_SLOT; stoneId++) {
                GameSlotStone stone = new GameSlotStone(stoneId);
                slotStones.add(stone);
            }

            GameSlot slot = new GameSlot(slotId, slotOwner, slotStones);

            slots.put(slotId, slot);
        }

        return slots;
    }

    private boolean slotIsUserStorage(GameSlot slot) {
        return slot == userAStorageSlot || slot == userBStorageSlot;
    }
}
