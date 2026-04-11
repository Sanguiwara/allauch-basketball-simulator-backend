package service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.sanguiwara.badges.BadgeEngine;
import com.sanguiwara.baserecords.*;
import com.sanguiwara.calculator.*;
import com.sanguiwara.calculator.spec.DriveSpecification;
import com.sanguiwara.calculator.spec.ThreePointSpecification;
import com.sanguiwara.calculator.spec.TwoPointSpecification;
import com.sanguiwara.defense.*;
import com.sanguiwara.gameevent.DriveEvent;
import com.sanguiwara.gameevent.ThreePointShotEvent;
import com.sanguiwara.gameevent.TwoPointShotEvent;
import com.sanguiwara.result.DriveResult;
import com.sanguiwara.result.GameResult;
import com.sanguiwara.result.ThreePointShootingResult;
import com.sanguiwara.result.TwoPointShootingResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class EnduranceAdvantageSimulationTest {

    private static final long SEED = 123456789L;
    private static final int MATCH_COUNT = 50;

    @Test
    void simulate50Games_all60_everywhere_except_endurance_A90_vs_B60_logsScoresAndWinner() {
        // Ensure INFO logs show up during tests (useful for this "simulation" style test).
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        Random random = new Random(SEED);
        GameSimulator simulator = createGameSimulator(random);

        int teamAWins = 0;
        int teamBWins = 0;
        int ties = 0;

        for (int i = 1; i <= MATCH_COUNT; i++) {
            GamePlans plans = makePlansAll60ExceptEndurance(i);
            GameResult result = simulator.calculateGame(plans.home(), plans.away());

            int aPts = result.homeScore().totalPoints();
            int bPts = result.awayScore().totalPoints();

            String winner;
            if (aPts > bPts) {
                teamAWins++;
                winner = "A";
            } else if (bPts > aPts) {
                teamBWins++;
                winner = "B";
            } else {
                ties++;
                winner = "TIE";
            }

            log.info("Match #{}: A {} - {} B (winner={})", i, aPts, bPts, winner);
        }

        log.info("Summary (seed={}): A_wins={}, B_wins={}, ties={}", SEED, teamAWins, teamBWins, ties);

        // Expect endurance advantage to matter: A should win more often than B (deterministic with seed).
        assertTrue(teamAWins > teamBWins, "Expected team A to win more often than team B");
    }

    private record GamePlans(GamePlan home, GamePlan away) {}

    private static GameSimulator createGameSimulator(Random random) {
        BadgeEngine badgeEngine = new BadgeEngine();
        List<DefensiveScheme> schemes = List.of(
                new RegularMan2ManScheme(badgeEngine),
                new Zone23Scheme(badgeEngine),
                new Zone212Scheme(badgeEngine),
                new Zone32Scheme(badgeEngine)
        );
        DefenseSchemeResolver defenseSchemeResolver = new DefenseSchemeResolver(schemes);

        AssistCalculator assistCalculator = new AssistCalculator(defenseSchemeResolver);
        ShotSimulator<ThreePointShotEvent, ThreePointShootingResult> threePointSimulator =
                new ShotSimulator<>(random, new ThreePointSpecification(random, badgeEngine), defenseSchemeResolver);
        ShotSimulator<TwoPointShotEvent, TwoPointShootingResult> twoPointSimulator =
                new ShotSimulator<>(random, new TwoPointSpecification(random, badgeEngine), defenseSchemeResolver);
        ShotSimulator<DriveEvent, DriveResult> driveSimulator =
                new ShotSimulator<>(random, new DriveSpecification(random, badgeEngine), defenseSchemeResolver);
        ReboundCalculator reboundCalculator = new ReboundCalculator(random, badgeEngine);
        BlockCalculator blockCalculator = new BlockCalculator(badgeEngine);
        StealSimulator stealSimulator = new StealSimulator(random, badgeEngine);

        return new GameSimulator(
                threePointSimulator,
                twoPointSimulator,
                driveSimulator,
                assistCalculator,
                reboundCalculator,
                blockCalculator,
                stealSimulator
        );
    }

    private static GamePlans makePlansAll60ExceptEndurance(int matchIndex) {
        Team teamA = new Team(UUID.randomUUID(), AgeCategory.SENIOR, Gender.MALE, "TEAM_A_" + matchIndex);
        Team teamB = new Team(UUID.randomUUID(), AgeCategory.SENIOR, Gender.MALE, "TEAM_B_" + matchIndex);

        List<Player> aPlayers = new ArrayList<>();
        List<Player> bPlayers = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            aPlayers.add(playerAll60ExceptEndurance("A_P" + i + "_M" + matchIndex, 90));
            bPlayers.add(playerAll60ExceptEndurance("B_P" + i + "_M" + matchIndex, 10));
        }
        teamA.setPlayers(aPlayers);
        teamB.setPlayers(bPlayers);

        GamePlan aPlan = new GamePlan(UUID.randomUUID(), teamA, teamB);
        GamePlan bPlan = new GamePlan(UUID.randomUUID(), teamB, teamA);

        aPlan.setDefenseType(DefenseType.MAN_TO_MAN);
        bPlan.setDefenseType(DefenseType.MAN_TO_MAN);
        aPlan.setMatchups(Matchups.empty()); // keep empty on purpose (see Man2ManScheme DEFAULT_CONTRIBUTION)
        bPlan.setMatchups(Matchups.empty());

        aPlan.setTotalShotNumber(75);
        bPlan.setTotalShotNumber(75);

        List<InGamePlayer> aActive = new ArrayList<>();
        List<InGamePlayer> bActive = new ArrayList<>();
        for (Player p : aPlayers) {
            InGamePlayer igp = new InGamePlayer(p, aPlan.getId());
            igp.setMinutesPlayed(40);
            igp.setStarter(true);
            aActive.add(igp);
        }
        for (Player p : bPlayers) {
            InGamePlayer igp = new InGamePlayer(p, bPlan.getId());
            igp.setMinutesPlayed(40);
            igp.setStarter(true);
            bActive.add(igp);
        }
        aPlan.setActivePlayers(aActive);
        bPlan.setActivePlayers(bActive);

        return new GamePlans(aPlan, bPlan);
    }

    private static Player playerAll60ExceptEndurance(String name, int endurance) {
        int v = 60;
        return Player.builder()
                .teamsID(java.util.Set.of())
                .clubID(UUID.randomUUID())
                .badgeIds(java.util.Set.of())
                .id(UUID.randomUUID())
                .name(name)
                .birthDate(2000)
                .injured(false)
                .tir3Pts(endurance)
                .tir2Pts(v)
                .lancerFranc(v)
                .floater(v)
                .finitionAuCercle(v)
                .speed(v)
                .ballhandling(v)
                .size(v)
                .weight(v)
                .agressivite(v)
                .defExterieur(v)
                .defPoste(v)
                .protectionCercle(v)
                .timingRebond(v)
                .agressiviteRebond(v)
                .steal(v)
                .timingBlock(v)
                .physique(v)
                .basketballIqOff(v)
                .basketballIqDef(v)
                .passingSkills(v)
                .iq(v)
                .endurance(v)
                .solidite(v)
                .potentielSkill(v)
                .potentielPhysique(v)
                .coachability(v)
                .ego(v)
                .softSkills(v)
                .leadership(v)
                .morale(v)
                .build();
    }
}
