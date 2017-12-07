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
    private Random randomness;

    // For quick find
    private Map<Integer, GameSlot> slots;

    // For making iterators
    private Collection<GameSlot> slotsCollection;

    public Game(String name) {
        this.name = name;
    }

    public GamePlayOutcome startGame() {
        if (BOARD_SLOTS % 2 > 0 || BOARD_SLOTS < 4) {
            // Safety
            throw new RuntimeException("BOARD_SLOTS must be divisible by 2 and gt 4!");
        }

        if (!isFull()) {
            throw new GameIsNotFullException();
        } else if (isGameStarted()) {
            throw new GameAlreadyStartedException();
        }

        this.randomness = new Random();

        bootstrapSlots();
        this.slotsCollection = this.slots.values();
        this.userAStorageSlot = this.slots.get(BOARD_SLOTS / 2);
        this.userBStorageSlot = this.slots.get(BOARD_SLOTS);

        setGameStarted(true);

        GamePlayOutcome initialPlayOutcome = new GamePlayOutcome();

        String firstTurnUserName = getRandomUserName();
        initialPlayOutcome.setNextTurnHolderUserName(firstTurnUserName);

        List<GameSlot> initialGameSlots = getSlots();
        initialPlayOutcome.setSlots(initialGameSlots);

        return initialPlayOutcome;
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

        if (!isGameStarted()) {
            throw new GameNotStartedException();
        }

        if (isGameFinished()) {
            throw new GameFinishedException();
        }

        GameSlot slot = this.getSlotWithId(slotId);

        String nextTurnHolderUserName = getOpponentUserNameForUser(user);

        GamePlayOutcome playOutcome = new GamePlayOutcome();

        Iterator<GameSlot> slotIterator = getIteratorAtSlot(slot);

        LinkedList<GameSlotStone> userStones = slot.pickStones();

        boolean userWon = false;

        while (!userStones.isEmpty() && !userWon) {

            GameSlot nextSlot = slotIterator.next();

            if (slotBelongsToUser(slot, user)) {
                if (userStones.size() == 1) {
                    // Last one
                    if (slotIsUserStorage(nextSlot)) {
                        // Drop it and extra turn!
                        GameSlotStone userStoneToAddToSlot = userStones.pop();
                        nextSlot.dropStone(userStoneToAddToSlot);

                        nextTurnHolderUserName = user.getName();
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
                    onGameFinished();
                    playOutcome.setGameFinished(true);
                    playOutcome.setWinnerUserName(user.getName());
                }
            }
        }

        playOutcome.setNextTurnHolderUserName(nextTurnHolderUserName);

        List<GameSlot> latestGameSlots = getSlots();

        playOutcome.setSlots(latestGameSlots);

        return playOutcome;
    }

    public GamePlayOutcome terminateGameForUser(User user) {
        if (!isGameStarted()) {
            throw new GameNotStartedException();
        }
        GamePlayOutcome gamePlayOutcome = new GamePlayOutcome();
        gamePlayOutcome.setGameFinished(true);

        String opponentUserName = getOpponentUserNameForUser(user);
        gamePlayOutcome.setWinnerUserName(opponentUserName);

        onGameFinished();
        return gamePlayOutcome;
    }

    public void addUser(User user) {
        if (isFull()) throw new GameFullException();
        if (userA == null) {
            userA = user;
        } else if (userB == null) {
            userB = user;
        }
    }

    public List<GameSlot> getSlots() {
        List<GameSlot> clonedSlots = new ArrayList<>();
        for (Map.Entry<Integer, GameSlot> map : slots.entrySet()) {
            GameSlot slot = map.getValue();
            clonedSlots.add(slot.clone());
        }
        return clonedSlots;
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

    private void onGameFinished() {
        setGameFinished(true);
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

            GameSlot slot = new GameSlot(slotId, slotStones);

            List<GameSlot> userNormalSlots = userBIsOwner ? userBNormalSlots : userANormalSlots;
            userNormalSlots.add(slot);

            slots.put(slotId, slot);
        }

        this.slots = slots;
    }

    private boolean normalSlotsAreEmptyForUser(User user) {
        boolean normalSlotsAreEmpty = true;
        List<GameSlot> userNormalSlots = getNormalSlotsForUser(user);
        for (GameSlot slot : userNormalSlots) {
            if (!slot.isEmpty()) {
                normalSlotsAreEmpty = false;
                break;
            }
        }
        return normalSlotsAreEmpty;
    }


    private String getOpponentUserNameForUser(User user) {
        User opponent = getOpponentForUser(user);
        return opponent.getName();
    }

    private User getOpponentForUser(User user) {
        User opponent = null;
        if (isUserB(user)) {
            opponent = userA;
        } else {
            opponent = userB;
        }

        return opponent;
    }

    private boolean isUserB(User user) {
        return user == getUserB();
    }

    private boolean slotIsUserStorage(GameSlot slot) {
        return slot == userAStorageSlot || slot == userBStorageSlot;
    }

    private boolean slotBelongsToUser(GameSlot slot, User user) {
        return slot == getStorageSlotForUser(user) || getNormalSlotsForUser(user).contains(slot);
    }

    private List<GameSlot> getNormalSlotsForUser(User user) {
        List<GameSlot> normalSlots;

        if (isUserB(user)) {
            normalSlots = userBNormalSlots;
        } else {
            normalSlots = userANormalSlots;
        }

        return normalSlots;
    }

    private String getRandomUserName() {
        User user = getRandomUser();
        return user.getName();
    }

    private User getRandomUser() {
        return randomness.nextBoolean() ? userA : userB;
    }

}
