package com.lifenautjoe.bol.domain;

import com.google.common.collect.Iterables;
import com.lifenautjoe.bol.domain.exceptions.GameAlreadyStartedException;
import com.lifenautjoe.bol.domain.exceptions.GameNotStartedException;
import com.lifenautjoe.bol.domain.exceptions.RequiredUsersNotSet;
import com.lifenautjoe.bol.domain.exceptions.UserAlreadySetException;

import java.util.*;

public class Game {

    private static final int BOARD_SLOTS = 14;
    private static final int STONES_PER_BOARD_SLOT = 6;
    private User userA;
    private User userB;
    private GameSlot userAStorageSlot;
    private GameSlot userBStorageSlot;
    private boolean gameStarted;
    private String name;

    // For quick find
    private Map<Integer, GameSlot> slots;

    // For making iterators
    private Collection<GameSlot> slotsCollection;

    public Game(String name) {
        this.name = name;
    }

    public void startGame() {
        if (!hasRequiredUsers()) {
            throw new RequiredUsersNotSet();
        } else if (isGameStarted()) {
            throw new GameAlreadyStartedException();
        }

        this.slots = makeSlots();
        this.slotsCollection = this.slots.values();
        this.userAStorageSlot = this.slots.get(BOARD_SLOTS / 2);
        this.userBStorageSlot = this.slots.get(BOARD_SLOTS);

        setGameStarted(true);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!Game.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Game other = (Game) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public GamePlayOutcome playAtSlotWithIdForUser(int slotId, User user) {
        GameSlot slot = this.getSlotWithId(slotId);
        return this.playAtSlotForUser(slot, user);
    }

    public GamePlayOutcome playAtSlotForUser(GameSlot slot, User user) {

        if (!isGameStarted()) {
            throw new GameNotStartedException();
        }

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

    public void setUserA(User userA) throws UserAlreadySetException {
        if (userA != null) throw new UserAlreadySetException();
        this.userA = userA;
    }

    public User getUserB() {
        return userB;
    }

    public void setUserB(User userB) throws UserAlreadySetException {
        if (userB != null) throw new UserAlreadySetException();
        this.userB = userB;
    }

    public String getName() {
        return name;
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

    private boolean hasRequiredUsers() {
        return userA != null && userB != null;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }
}
