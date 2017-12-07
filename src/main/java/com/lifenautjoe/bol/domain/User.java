package com.lifenautjoe.bol.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lifenautjoe.bol.domain.exceptions.UserHasNoGameException;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.Objects;

public class User implements Cloneable, Serializable {
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

    @Override
    public User clone() {
        return SerializationUtils.clone(this);
    }

    public String getName() {
        return name;
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
        return this.game != null;
    }

    public boolean hasGameWithName(String gameName) {
        if (!hasGame()) return false;
        return game.getName().equals(gameName);
    }

    public GamePlayOutcome terminateGame() {
        Game game = getGame();
        GamePlayOutcome gamePlayOutcome = game.terminateGameForUser(this);
        removeGame();
        return gamePlayOutcome;
    }

    public String getGameName() {
        Game game = getGame();
        return game.getName();
    }

    private void removeGame() {
        this.game = null;
    }

    private Game getGame() {
        if (!hasGame()) {
            throw new UserHasNoGameException();
        }
        return game;
    }
}
