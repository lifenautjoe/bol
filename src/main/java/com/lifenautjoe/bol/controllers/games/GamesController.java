package com.lifenautjoe.bol.controllers.games;

import com.lifenautjoe.bol.domain.Game;
import com.lifenautjoe.bol.domain.GamePlayOutcome;
import com.lifenautjoe.bol.domain.User;
import com.lifenautjoe.bol.services.games.GamesManagerService;
import com.lifenautjoe.bol.services.users.UsersManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpSession;
import java.util.Collection;

@RestController
@RequestMapping(path = "/games")
public class GamesController {
    private SimpMessagingTemplate template;
    private GamesManagerService gamesManagerService;
    private UsersManagerService usersManagerService;

    @Autowired
    public GamesController(SimpMessagingTemplate messagingTemplate,
                           GamesManagerService gamesManagerService,
                           UsersManagerService usersManagerService) {
        this.template = messagingTemplate;
        this.gamesManagerService = gamesManagerService;
        this.usersManagerService = usersManagerService;
    }


    @RequestMapping(path = "")
    public Collection<Game> getAll() {
        return gamesManagerService.getAll();
    }

    @RequestMapping(path = "/{gameName}/join")
    public ResponseEntity<?> joinGame(@PathVariable("gameName") String gameName, HttpSession httpSession) {
        if (!usersManagerService.sessionHasUser(httpSession)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("User is not logged in");
        }

        User user = usersManagerService.getUserFromSession(httpSession);

        if (user.hasGame()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("User already has game");
        }

        Game game = gamesManagerService.getOrCreateGameWithName(gameName);

        if (game.isFull()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Game is full");
        }

        user.setGame(game);

        return ResponseEntity.ok("Game joined!");
    }

    @RequestMapping(path = "/{gameName}/play", method = RequestMethod.POST)
    public ResponseEntity gamePlay(@PathVariable() String gameName,
                                   @RequestBody() GamePlayRequestBody body,
                                   HttpSession httpSession) {
        if (!usersManagerService.sessionHasUser(httpSession)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("User is not logged in");
        }

        int slotIdToPlayAt = body.getSlotId();

        User user = usersManagerService.getUserFromSession(httpSession);

        if (!user.hasGameWithName(gameName)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("User is not in given game");
        }

        GamePlayOutcome playOutcome = user.playGameAtSlotWithId(slotIdToPlayAt);

        if (playOutcome.isGameFinished()) {
            gamesManagerService.removeGameWithName(gameName);
        }

        this.template.convertAndSend("/games/" + gameName, playOutcome);
        return ResponseEntity.ok("Played!");
    }

}
