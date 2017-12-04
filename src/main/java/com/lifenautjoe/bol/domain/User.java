package com.lifenautjoe.bol.domain;

import com.lifenautjoe.bol.domain.exceptions.UserHasNoGameException;

import java.util.Objects;

public class User {
    private String name;
    private Game game;

    public User(String name) {
        this.name = name;
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
        if (!User.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final User other = (User) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
        game.addUser(this);
    }

    public GamePlayOutcome playGameAtSlotWithId(int slotId) {
        if (!hasGame()) {
            throw new UserHasNoGameException();
        }
        return game.playAtSlotWithIdForUser(slotId, this);
    }

    public boolean hasGame() {
        return this.game == null;
    }
}
