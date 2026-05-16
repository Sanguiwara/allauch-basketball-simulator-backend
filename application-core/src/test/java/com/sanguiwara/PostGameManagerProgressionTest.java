package com.sanguiwara;

import com.sanguiwara.baserecords.AgeCategory;
import com.sanguiwara.baserecords.Game;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.Gender;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.Team;
import com.sanguiwara.postgame.PostGameManager;
import com.sanguiwara.progression.PlayerProgression;
import com.sanguiwara.progression.manager.InactivityProgressionManager;
import com.sanguiwara.progression.manager.MoraleProgressionManager;
import com.sanguiwara.progression.manager.ReboundingProgressionManager;
import com.sanguiwara.progression.manager.ShootingSkillProgressionManager;
import com.sanguiwara.progression.manager.StocksProgressionManager;
import com.sanguiwara.result.BoxScore;
import com.sanguiwara.result.DriveResult;
import com.sanguiwara.result.GameResult;
import com.sanguiwara.result.ThreePointShootingResult;
import com.sanguiwara.result.TwoPointShootingResult;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PostGameManagerProgressionTest {

    @Test
    void applyPostGameEffectsAndReturnsPlayersProgression_includesRosterPlayersWhoDidNotPlay() {
        PostGameManager manager = new PostGameManager(
                new ShootingSkillProgressionManager(new Random(0)),
                new InactivityProgressionManager(),
                new ReboundingProgressionManager(new Random(0)),
                new StocksProgressionManager(new Random(0)),
                new MoraleProgressionManager()
        );

        Player homeActive = player("home active", 50);
        Player homeInactive = player("home inactive", 50);
        Player awayActive = player("away active", 50);
        Player awayInactive = player("away inactive", 50);

        Game game = gameWithRosters(homeActive, homeInactive, awayActive, awayInactive);

        List<PlayerProgression> progressions = manager.applyPostGameEffectsAndReturnsPlayersProgression(game);

        assertThat(homeInactive.getMorale()).isEqualTo(45);
        assertThat(awayInactive.getMorale()).isEqualTo(45);

        Map<UUID, PlayerProgression> progressionsByPlayerId = new HashMap<>();
        progressions.forEach(progression -> progressionsByPlayerId.put(progression.playerId(), progression));

        assertThat(progressionsByPlayerId.keySet())
                .contains(homeActive.getId(), homeInactive.getId(), awayActive.getId(), awayInactive.getId());
        assertThat(progressionsByPlayerId.get(homeInactive.getId()).delta().morale()).isEqualTo(-5);
        assertThat(progressionsByPlayerId.get(awayInactive.getId()).delta().morale()).isEqualTo(-5);
    }

    private static Game gameWithRosters(
            Player homeActive,
            Player homeInactive,
            Player awayActive,
            Player awayInactive
    ) {
        Team homeTeam = new Team(UUID.randomUUID(), AgeCategory.U18, Gender.MALE, "home");
        Team awayTeam = new Team(UUID.randomUUID(), AgeCategory.U18, Gender.MALE, "away");
        homeTeam.setPlayers(List.of(homeActive, homeInactive));
        awayTeam.setPlayers(List.of(awayActive, awayInactive));

        GamePlan homeGamePlan = new GamePlan(UUID.randomUUID(), homeTeam, awayTeam);
        GamePlan awayGamePlan = new GamePlan(UUID.randomUUID(), awayTeam, homeTeam);
        homeGamePlan.setActivePlayers(List.of(new InGamePlayer(homeActive, homeGamePlan.getId())));
        awayGamePlan.setActivePlayers(List.of(new InGamePlayer(awayActive, awayGamePlan.getId())));

        Game game = new Game(UUID.randomUUID(), homeGamePlan, awayGamePlan, null, Instant.parse("2026-01-01T00:00:00Z"));
        game.setGameResult(homeWinResult());
        return game;
    }

    private static Player player(String name, int morale) {
        return Player.builder()
                .id(UUID.randomUUID())
                .name(name)
                .birthDate(2000)
                .badgeIds(new HashSet<>())
                .tir3Pts(50)
                .tir2Pts(50)
                .lancerFranc(50)
                .floater(50)
                .finitionAuCercle(50)
                .speed(50)
                .ballhandling(50)
                .size(50)
                .weight(50)
                .agressivite(50)
                .defExterieur(50)
                .defPoste(50)
                .protectionCercle(50)
                .timingRebond(50)
                .agressiviteRebond(50)
                .steal(50)
                .timingBlock(50)
                .physique(50)
                .basketballIqOff(50)
                .basketballIqDef(50)
                .passingSkills(50)
                .iq(50)
                .endurance(50)
                .solidite(50)
                .potentielSkill(50)
                .potentielPhysique(50)
                .coachability(50)
                .ego(50)
                .softSkills(50)
                .leadership(50)
                .morale(morale)
                .build();
    }

    private static GameResult homeWinResult() {
        BoxScore homeScore = new BoxScore(
                new ThreePointShootingResult(1, 1, List.of()),
                DriveResult.empty(),
                TwoPointShootingResult.empty()
        );
        BoxScore awayScore = new BoxScore(
                ThreePointShootingResult.empty(),
                DriveResult.empty(),
                TwoPointShootingResult.empty()
        );
        return new GameResult(homeScore, awayScore);
    }
}
