package com.lifenautjoe.bol.services.games;

import com.lifenautjoe.bol.Mappings;
import com.lifenautjoe.bol.domain.GamePlayOutcome;
import com.lifenautjoe.bol.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class GamesRealtimeService {

    private GamesRepositoryService gamesRepositoryService;
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public GamesRealtimeService(GamesRepositoryService gamesRepositoryService, SimpMessagingTemplate messagingTemplate) {
        this.gamesRepositoryService = gamesRepositoryService;
        this.messagingTemplate = messagingTemplate;
    }

    public void terminateGameForUser(User user) {
        String gameName = user.getGameName();
        GamePlayOutcome gamePlayOutcome = user.terminateGame();
        sendGamePlayOutcome(gamePlayOutcome);
        gamesRepositoryService.removeGameWithName(gameName);
    }

    public void startGameForUser(User user) {
        GamePlayOutcome gamePlayOutcome = user.startGame();
        sendGamePlayOutcome(gamePlayOutcome);
    }

    public void playGameAtSlotWithIdForUser(int slotId, User user) {
        GamePlayOutcome playOutcome = user.playGameAtSlotWithId(slotId);

        if (playOutcome.isGameFinished()) {
            terminateGameForUser(user);
        } else {
            sendGamePlayOutcome(playOutcome);
        }
    }

    private void sendGamePlayOutcome(GamePlayOutcome gamePlayOutcome) {
        String gameName = gamePlayOutcome.getGameName();
        messagingTemplate.convertAndSend(Mappings.REALTIME_GAMES + '/' + gameName, gamePlayOutcome);
    }
}
