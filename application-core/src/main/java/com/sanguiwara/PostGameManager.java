package com.sanguiwara;

import com.sanguiwara.baserecords.Game;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.progression.PlayerProgression;
import com.sanguiwara.progression.PlayerProgressionDelta;
import com.sanguiwara.progression.ProgressionEventType;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PostGameManager {

    private final ShootingSkillProgressionManager shootingSkillProgressionManager = new ShootingSkillProgressionManager();
    private final InactivityProgressionManager inactivityProgressionManager = new InactivityProgressionManager();
    private final ReboundingProgressionManager reboundingProgressionManager = new ReboundingProgressionManager();
    private final StocksProgressionManager stocksProgressionManager = new StocksProgressionManager();
    private final MoraleProgressionManager moraleProgressionManager = new MoraleProgressionManager();

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
        applyProgressionForGamePlan(winningGamePlan);
        applyProgressionForGamePlan(losingGamePlan);
    }

    public List<PlayerProgression> applyPostGameEffectsAndReturnsPlayersProgression(Game game) {
        List<InGamePlayer> all = new ArrayList<>();
        all.addAll(game.getHomeGamePlan().getActivePlayers());
        all.addAll(game.getAwayGamePlan().getActivePlayers());

        Map<UUID, Player> beforeByPlayerId = new HashMap<>(all.size());
        for (InGamePlayer inGamePlayer : all) {
            Player player = inGamePlayer.getPlayer();
            beforeByPlayerId.put(player.getId(), player.snapshotPlayer());
        }

        applyPostGameEffects(game);
        List<PlayerProgression> progressionList = new ArrayList<>();

        for (InGamePlayer inGamePlayer : all) {
            Player playerAfterProgress = inGamePlayer.getPlayer();
            Player playerBeforeProgress = beforeByPlayerId.get(playerAfterProgress.getId());
            PlayerProgressionDelta delta = PlayerProgressionDelta.between(playerBeforeProgress, playerAfterProgress);
            progressionList.add(new PlayerProgression(playerAfterProgress.getId(), ProgressionEventType.GAME, game.getId(), delta));
        }

        return progressionList;
    }


    private void applyProgressionForGamePlan(GamePlan gamePlan) {

        for (InGamePlayer inGamePlayer : gamePlan.getActivePlayers()) {

            moraleProgressionManager.applyMoraleFromPerformance(inGamePlayer);
            applyProgression(inGamePlayer);

        }
    }



    private void applyProgression(InGamePlayer inGamePlayer) {
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
