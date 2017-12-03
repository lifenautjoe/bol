package com.lifenautjoe.bol.controllers.games;

import com.lifenautjoe.bol.domain.Game;
import com.lifenautjoe.bol.domain.GamePlayOutcome;
import com.lifenautjoe.bol.domain.User;
import com.lifenautjoe.bol.services.GamesManagerService;
import com.lifenautjoe.bol.services.UsersManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpSession;
import java.util.List;

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
    public List<Game> getAll() {
        return gamesManagerService.getAll();
    }

    @RequestMapping(path = "/{gameId}/join")
    public void joinGame(@PathVariable("gameId") int gameId, HttpSession httpSession) {

    }

    @RequestMapping(path = "/{gameId}/play", method = RequestMethod.POST)
    public void gamePlay(@PathVariable("gameId") int gameId,
                     @RequestBody() GamePlayRequestBody body,
                     HttpSession httpSession) {
        User user = usersManagerService.getUserFromSession(httpSession);
        Game userGame = user.getCurrentGame();
        GamePlayOutcome playOutcome = userGame.playAtSlotWithIdForUser(body.getSlotId(), user);
        this.template.convertAndSend("/games/" + gameId, playOutcome);
    }

}
