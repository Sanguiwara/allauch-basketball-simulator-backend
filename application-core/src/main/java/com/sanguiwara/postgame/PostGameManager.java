package com.sanguiwara.postgame;

import com.sanguiwara.baserecords.Game;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.progression.PlayerProgression;
import com.sanguiwara.progression.PlayerProgressionDelta;
import com.sanguiwara.progression.ProgressionEventType;
import com.sanguiwara.progression.manager.InactivityProgressionManager;
import com.sanguiwara.progression.manager.MoraleProgressionManager;
import com.sanguiwara.progression.manager.ReboundingProgressionManager;
import com.sanguiwara.progression.manager.ShootingSkillProgressionManager;
import com.sanguiwara.progression.manager.StocksProgressionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostGameManager {

    private final ShootingSkillProgressionManager shootingSkillProgressionManager;
    private final InactivityProgressionManager inactivityProgressionManager;
    private final ReboundingProgressionManager reboundingProgressionManager;
    private final StocksProgressionManager stocksProgressionManager;
    private final MoraleProgressionManager moraleProgressionManager;

    public List<PlayerProgression> applyPostGameEffectsAndReturnsPlayersProgression(Game game) {
        List<PlayerProgression> progressionList = new ArrayList<>();
        List<Player> playersFromGame = PostGamePlayerResolver.resolveAffectedPlayers(game);

        Map<UUID, Player> beforeByPlayerId =
                playersFromGame.stream()
                        .collect(Collectors.toMap(
                                Player::getId,
                                Player::snapshotPlayer,
                                (first, ignored) -> first,
                                LinkedHashMap::new
                        ));

        applyPostGameEffects(game);

        for (Player playerAfterProgress : playersFromGame) {
            Player playerBeforeProgress = beforeByPlayerId.get(playerAfterProgress.getId());
            PlayerProgressionDelta delta = PlayerProgressionDelta.between(playerBeforeProgress, playerAfterProgress);
            // Store only badges earned during this event (not the full post-event badge set).
            var badgeIds = delta.badgesAdded();
            progressionList.add(new PlayerProgression(
                    playerAfterProgress.getId(),
                    ProgressionEventType.GAME,
                    game.getId(),
                    badgeIds,
                    delta.temporaryModifiersAdded(),
                    delta
            ));
        }

        return progressionList;
    }








    public void applyPostGameEffects(Game game) {

        GamePlan winningGamePlan;
        GamePlan losingGamePlan;

        if (homeTeamWon(game)) {
            winningGamePlan = game.getHomeGamePlan();
            losingGamePlan = game.getAwayGamePlan();
        } else {
            winningGamePlan = game.getAwayGamePlan();
            losingGamePlan = game.getHomeGamePlan();
        }
        moraleProgressionManager.applyLosingEffect(losingGamePlan);
        moraleProgressionManager.applyWinningEffect(winningGamePlan);
        moraleProgressionManager.applyDidNotPlayPenalty(losingGamePlan);
        moraleProgressionManager.applyDidNotPlayPenalty(winningGamePlan);

        winningGamePlan.getActivePlayers().forEach(this::applyProgression);
        losingGamePlan.getActivePlayers().forEach(this::applyProgression);
    }


    private void applyProgression(InGamePlayer inGamePlayer) {
        moraleProgressionManager.applyMoraleFromPerformance(inGamePlayer);
        inactivityProgressionManager.apply(inGamePlayer);
        shootingSkillProgressionManager.applyShootingSkillProgression(inGamePlayer);
        reboundingProgressionManager.applyReboundingProgression(inGamePlayer);
        stocksProgressionManager.applyStocksProgression(inGamePlayer);
    }


    private boolean homeTeamWon(Game game) {
        int homeScore = game.getGameResult().homeScore().totalPoints();
        int awayScore = game.getGameResult().awayScore().totalPoints();
        return homeScore > awayScore;
    }

}
