package com.lifenautjoe.bol.controllers.games;

import com.lifenautjoe.bol.controllers.ApiResponse;
import com.lifenautjoe.bol.controllers.games.requests.GamePlayRequest;
import com.lifenautjoe.bol.controllers.games.requests.JoinGameRequest;
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
@RequestMapping(path = "/api/games")
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


    @RequestMapping(path = "", method = RequestMethod.GET)
    public ResponseEntity<Object> getAll() {
        Collection<Game> games = gamesManagerService.getAll();
        List<GameResponse> responseGames = new ArrayList<>();

        for (Game game : games) {
            GameResponse gameReponse = conversionService.convert(game, GameResponse.class);
            responseGames.add(gameReponse);
        }
        return ResponseEntity.ok().body(responseGames);
    }

    @RequestMapping(path = "/join", method = RequestMethod.POST)
    public ResponseEntity<Object> joinGame(@RequestBody() JoinGameRequest body,
                                           HttpSession httpSession) {

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

        String gameName = body.getGameName();

        if (!gamesManagerService.hasGameWithName(gameName)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Game does not exist"));
        }

        Game game = gamesManagerService.getGameWithName(gameName);

        if (game.isFull()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse("Game is full"));
        }

        user.setGame(game);

        return ResponseEntity.ok(new ApiResponse("Game joined!"));
    }

    @RequestMapping(path = "/create", method = RequestMethod.PUT)
    public ResponseEntity<Object> createGame(@RequestBody() JoinGameRequest body,
                                             HttpSession httpSession) {

        if (!usersManagerService.sessionHasUser(httpSession)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse("User must be logged in before creating a game"));
        }

        User user = usersManagerService.getUserFromSession(httpSession);

        if (user.hasGame()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ApiResponse("User already has a game"));
        }

        String gameName = body.getGameName();

        if (gamesManagerService.hasGameWithName(gameName)) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ApiResponse("Game already exists!"));
        }

        Game game = gamesManagerService.createGameWithName(gameName);

        user.setGame(game);

        return ResponseEntity.ok(new ApiResponse("Game created!"));
    }

    @RequestMapping(path = "/play", method = RequestMethod.POST)
    public ResponseEntity gamePlay(@RequestBody() GamePlayRequest body,
                                   HttpSession httpSession) {
        if (!usersManagerService.sessionHasUser(httpSession)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("User is not logged in");
        }


        String gameName = body.getGameName();
        User user = usersManagerService.getUserFromSession(httpSession);

        if (!user.hasGameWithName(gameName)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("User is not in given game");
        }

        int slotIdToPlayAt = body.getSlotId();

        GamePlayOutcome playOutcome = user.playGameAtSlotWithId(slotIdToPlayAt);

        if (playOutcome.isGameFinished()) {
            gamesManagerService.removeGameWithName(gameName);
        }

        this.template.convertAndSend("/games/" + gameName, playOutcome);
        return ResponseEntity.ok("Played!");
    }

}
