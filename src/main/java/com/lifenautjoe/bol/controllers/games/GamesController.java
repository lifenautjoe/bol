package com.lifenautjoe.bol.controllers.games;

import com.lifenautjoe.bol.controllers.ApiResponse;
import com.lifenautjoe.bol.controllers.games.requests.GamePlayRequestBody;
import com.lifenautjoe.bol.controllers.games.responses.GameResponse;
import com.lifenautjoe.bol.domain.Game;
import com.lifenautjoe.bol.domain.GamePlayOutcome;
import com.lifenautjoe.bol.domain.User;
import com.lifenautjoe.bol.services.games.GamesManagerService;
import com.lifenautjoe.bol.services.users.UsersManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(path = "/games")
public class GamesController {
    private SimpMessagingTemplate template;
    private GamesManagerService gamesManagerService;
    private UsersManagerService usersManagerService;
    private ConversionService conversionService;

    @Autowired
    public GamesController(SimpMessagingTemplate messagingTemplate,
                           GamesManagerService gamesManagerService,
                           UsersManagerService usersManagerService,
                           ConversionService conversionService) {
        this.template = messagingTemplate;
        this.gamesManagerService = gamesManagerService;
        this.usersManagerService = usersManagerService;
        this.conversionService = conversionService;
    }


    @RequestMapping(path = "")
    public ResponseEntity<Object> getAll() {
        Collection<Game> games = gamesManagerService.getAll();
        List<GameResponse> responseGames = new ArrayList<>();

        for (Game game : games) {
            GameResponse gameReponse = conversionService.convert(game, GameResponse.class);
            responseGames.add(gameReponse);
        }
        return ResponseEntity.ok().body(responseGames);
    }

    @RequestMapping(path = "/{gameName}/join")
    public ResponseEntity<Object> joinGame(@PathVariable("gameName") String gameName, HttpSession httpSession) {

        if (!usersManagerService.sessionHasUser(httpSession)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse("User must be logged in before joining a game"));
        }

        User user = usersManagerService.getUserFromSession(httpSession);

        if (user.hasGame()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ApiResponse("User already has a game"));
        }

        Game game = gamesManagerService.getOrCreateGameWithName(gameName);

        if (game.isFull()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse("Game is full"));
        }

        user.setGame(game);

        return ResponseEntity.ok(new ApiResponse("Game joined!"));
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
