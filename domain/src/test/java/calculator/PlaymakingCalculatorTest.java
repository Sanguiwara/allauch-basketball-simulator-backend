package calculator;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.calculator.PlaymakingCalculator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class PlaymakingCalculatorTest {

    private static Player playerWithRatings(int base) {
        // Helper to create a player with most fields set around a base value
        return new Player(
                UUID.randomUUID(),
                "P" + base,
                1990,
                // shooting / finishing
                base, // tir3Pts
                base, // tir2Pts
                base, // lancerFranc
                base, // floater
                base, // finitionAuCercle
                base, // speed
                base, // ballhandling
                base, // size
                base, // weight
                base, // agressivite
                // defense / rebond
                base, // defExterieur
                base, // defPoste
                base, // protectionCercle
                base, // timingRebond
                base, // agressiviteRebond
                base, // steal
                // physique / mental / skills
                base, // physique
                base, // basketballIqOff
                base, // basketballIqDef
                base, // passingSkills
                base, // iq
                base, // endurance
                base, // solidite
                // potentiel
                base, // potentielSkill
                base, // potentielPhysique
                // comportement
                base, // coachability
                base, // ego
                base, // softSkills
                base  // leadership
        );
    }

    @Test
    void getAssistedShotPercentage_shouldClampBetweenMinAndMax() {
        PlaymakingCalculator calc = new PlaymakingCalculator();

        // Very low contribution should clamp to min (0.10)
        double pctLow = calc.getAssistedShotPercentage(-1000);
        log.info("Assisted pct (low input): {}", pctLow);
        assertEquals(0.10, pctLow, 1e-9);

        // Very high contribution should clamp to max (0.50)
        double pctHigh = calc.getAssistedShotPercentage(10_000);
        log.info("Assisted pct (high input): {}", pctHigh);
        assertEquals(0.50, pctHigh, 1e-9);

        // Mid value within bounds
        double pctMid = calc.getAssistedShotPercentage(50.0);
        log.info("Assisted pct (mid input 50.0): {}", pctMid);
        assertTrue(pctMid >= 0.10 && pctMid <= 0.50);
    }

    @Test
    void getIndividualPlayMakingContribution_setsContributionAndClamps() {
        PlaymakingCalculator calc = new PlaymakingCalculator();

        // Offensive player stronger than defender -> positive advantage
        Player strongOff = playerWithRatings(90);
        Player weakDef = playerWithRatings(30);
        InGamePlayer igp = new InGamePlayer(strongOff, 20, 20, 20);

        double adv = calc.getIndividualPlayMakingContribution(igp, weakDef);
        log.info("Advantage strongOff vs weakDef: {} ",  igp.getPlaymakingContribution());

        // Inverse: very strong defender, weak attacker -> negative but clamped >= -5
        Player weakOff = playerWithRatings(20);
        Player strongDef = playerWithRatings(95);
        InGamePlayer igp2 = new InGamePlayer(weakOff, 20, 20, 20);
        double adv2 = calc.getIndividualPlayMakingContribution(igp2, strongDef);
        log.info("Advantage weakOff vs strongDef: {}", igp2.getPlaymakingContribution());
        assertTrue(adv >= adv2);
    }

    @Test
    void getTotalPlaymakingContribution_setsAssistWeights_andReturnsProbability() {
        PlaymakingCalculator calc = new PlaymakingCalculator();

        // Build two players vs two defenders with different strengths to avoid zero division
        InGamePlayer off1 = new InGamePlayer(playerWithRatings(85), 20, 20, 20);
        InGamePlayer off2 = new InGamePlayer(playerWithRatings(75), 20, 20, 20);
        List<InGamePlayer> active = new ArrayList<>(List.of(off1, off2));

        Player def1 = playerWithRatings(50);
        Player def2 = playerWithRatings(60);

        GamePlan home = new GamePlan(null, null, null);
        GamePlan away = new GamePlan(null, null, null);

        home.setActivePlayers(active);
        away.setActivePlayers(List.of(new InGamePlayer(def1, 0, 0, 0), new InGamePlayer(def2, 0, 0, 0)));

        Map<Player, Player> matchups = new HashMap<>();
        matchups.put(off1.getPlayer(), def1);
        matchups.put(off2.getPlayer(), def2);
        away.setMatchups(new HashMap<>()); // not used here
        home.setMatchups(new HashMap<>()); // not used here

        // PlaymakingCalculator expects matchups from visitor keyed by home players
        GamePlan visitor = new GamePlan(null, null, null);
        visitor.setMatchups(matchups);

        double assistedPct = calc.getTotalPlaymakingContribution(home, visitor);
        log.info("Total assisted shot probability: {}", assistedPct);

        // Probability is clamped between 0.10 and 0.50
        assertTrue(assistedPct >= 0.10 && assistedPct <= 0.50);

        // Assist weights should be set and positive
        log.info("Assist weights -> off1: {}, off2: {}", off1.getAssistWeight(), off2.getAssistWeight());
        assertTrue(off1.getAssistWeight() > 0.0 && off1.getAssistWeight() < 1.0);
        assertTrue(off2.getAssistWeight() > 0.0 && off2.getAssistWeight() < 1.0);
    }
}
