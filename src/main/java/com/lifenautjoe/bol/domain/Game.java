package com.lifenautjoe.bol.domain;

import com.google.common.collect.Iterables;
import com.lifenautjoe.bol.domain.exceptions.*;

import java.util.*;

public class Game {

    private static final int BOARD_SLOTS = 14;
    private static final int STONES_PER_BOARD_SLOT = 6;
    private User userA;
    private User userB;
    private GameSlot userAStorageSlot;
    private GameSlot userBStorageSlot;
    private List<GameSlot> userANormalSlots;
    private List<GameSlot> userBNormalSlots;
    private boolean gameStarted;
    private boolean gameFinished;
    private String name;

    // For quick find
    private Map<Integer, GameSlot> slots;

    // For making iterators
    private Collection<GameSlot> slotsCollection;

    public Game(String name) {
        this.name = name;
    }

    public void startGame() {
        if (BOARD_SLOTS % 2 > 0 || BOARD_SLOTS < 4) {
            // Safety
            throw new RuntimeException("BOARD_SLOTS must be divisible by 2 and gt 4!");
        }

        if (!isFull()) {
            throw new RequiredUsersNotSet();
        } else if (isGameStarted()) {
            throw new GameAlreadyStartedException();
        }

        bootstrapSlots();
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

        if (isGameFinished()) {
            throw new GameFinishedException();
        }

        GamePlayOutcome playOutcome = new GamePlayOutcome();

        if (isUserB(user)) {
            playOutcome.setNextTurnHolder(userA);
        } else {
            playOutcome.setNextTurnHolder(userB);
        }

        Iterator<GameSlot> slotIterator = getIteratorAtSlot(slot);

        LinkedList<GameSlotStone> userStones = slot.pickStones();

        boolean userWon = false;

        while (!userStones.isEmpty() && !userWon) {

            GameSlot nextSlot = slotIterator.next();

            if (nextSlot.belongsToUser(user)) {
                if (userStones.size() == 1) {
                    // Last one
                    if (slotIsUserStorage(nextSlot)) {
                        // Drop it and extra turn!
                        GameSlotStone userStoneToAddToSlot = userStones.pop();
                        nextSlot.dropStone(userStoneToAddToSlot);

                        playOutcome.setNextTurnHolder(user);
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

                if (normalSlotsAreEmptyForUser(user)) {
                    // Yay
                    userWon = true;
                    setGameFinished(true);
                    playOutcome.setGameFinished(true);
                    playOutcome.setWinner(user);
                }
            }
        }

        return playOutcome;
    }

    public void addUser(User user) {
        if (isFull()) throw new GameFullException();
        if (userA == null) {
            userA = user;
        } else if (userB == null) {
            userB = user;
        }
    }

    public User getUserA() {
        return userA;
    }

    public User getUserB() {
        return userB;
    }

    public String getName() {
        return name;
    }

    public boolean isFull() {
        return userA != null && userB != null;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public boolean isGameFinished() {
        return gameFinished;
    }

    public void setGameFinished(boolean gameFinished) {
        this.gameFinished = gameFinished;
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

    private void bootstrapSlots() {

        int slotsPerUser = BOARD_SLOTS / 2;

        Map<Integer, GameSlot> slots = new HashMap<>();

        for (int slotId = 1; slotId < BOARD_SLOTS; slotId++) {

            User slotOwner = null;
            boolean userBIsOwner = slotId > slotsPerUser;

            if (userBIsOwner) {
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

            List<GameSlot> userNormalSlots = userBIsOwner ? userBNormalSlots : userANormalSlots;
            userNormalSlots.add(slot);

            slots.put(slotId, slot);
        }

        this.slots = slots;
    }

    private boolean normalSlotsAreEmptyForUser(User user) {
        boolean normalSlotsAreEmpty = true;
        List<GameSlot> userNormalSlots = isUserB(user) ? userBNormalSlots : userANormalSlots;
        for (GameSlot slot : userNormalSlots) {
            if (!slot.isEmpty()) {
                normalSlotsAreEmpty = false;
                break;
            }
        }
        return normalSlotsAreEmpty;
    }

    private boolean isUserB(User user) {
        return user == getUserB();
    }

    private boolean slotIsUserStorage(GameSlot slot) {
        return slot == userAStorageSlot || slot == userBStorageSlot;
    }
}
