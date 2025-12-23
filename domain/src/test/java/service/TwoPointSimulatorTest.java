package service;

import ingame.GamePlan;
import ingame.InGamePlayer;
import baserecords.Player;
import event.TwoPointShotEvent;
import result.TwoPointShootingResult;
import org.junit.jupiter.api.Test;
import simulator.TwoPointSimulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TwoPointSimulatorTest {

    private static GamePlan makePlan(PlayerRandomFactory factory, String prefix, long startId) {
        // Création d'une équipe simple de 5 joueurs
        List<InGamePlayer> players = new ArrayList<>();
        players.add(new InGamePlayer(factory.random(startId + 0, prefix + "_PG"), 30, 15, 15));
        players.add(new InGamePlayer(factory.random(startId + 1, prefix + "_SG"), 15, 15, 15));
        players.add(new InGamePlayer(factory.random(startId + 2, prefix + "_SF"), 15, 15, 15));
        players.add(new InGamePlayer(factory.random(startId + 3, prefix + "_PF"), 15, 15, 15));
        players.add(new InGamePlayer(factory.random(startId + 4, prefix + "_C"), 15, 15, 15));

        // Pas de Team nécessaire pour ces tests
        return new GamePlan(null, null, players, null);
    }

    @Test
    void simulate2ptForMatchup_shouldProduceEventsAndPrint() {
        long seed = 12345L;
        PlayerRandomFactory factory = new PlayerRandomFactory(seed);
        GamePlan home = makePlan(factory, "HOME", 1);
        GamePlan away = makePlan(factory, "AWAY", 100);

        InGamePlayer shooter = home.getActivePlayers().get(1);
        InGamePlayer defender = away.getActivePlayers().get(1);

        TwoPointSimulator sim = new TwoPointSimulator(new Random(seed));

        TwoPointShootingResult res = sim.simulate2ptForMatchup(
                home, shooter, defender,
                0.35
        );

        assertNotNull(res);
        assertTrue(res.attempts() >= 0);
        assertTrue(res.made() >= 0 && res.made() <= res.attempts());
        assertEquals(res.attempts(), res.events().size());

        double pct = res.attempts() == 0 ? 0.0 : (100.0 * res.made() / res.attempts());
        System.out.println("[2PT MATCHUP] attempts=" + res.attempts() + ", made=" + res.made() + ", pct=" + String.format("%.1f", pct));
        for (TwoPointShotEvent e : res.events()) {
            System.out.println(e);
        }
    }

    @Test
    void simulateShots_shouldHonorAssistAndTypes() {
        long seed = 777L;
        PlayerRandomFactory factory = new PlayerRandomFactory(seed);
        List<InGamePlayer> players = new ArrayList<>();
        players.add(new InGamePlayer(factory.random(1, "A"), 30, 15, 15));
        players.add(new InGamePlayer(factory.random(2, "B"), 15, 15, 15));
        players.add(new InGamePlayer(factory.random(3, "C"), 15, 15, 15));

        InGamePlayer shooter = players.get(0);
        // Donner du poids de passe aux coéquipiers pour permettre des assists
        players.get(1).setAssistWeight(8.0);
        players.get(2).setAssistWeight(2.0);
        List<InGamePlayer> passers = players;

        TwoPointSimulator sim = new TwoPointSimulator(new Random(seed));
        TwoPointShootingResult res = sim.simulateShots(
                shooter,
                passers,
                10,
                0.80, // beaucoup de passes pour voir des assists
                0.05,
                10.0 // petit avantage
        );

        assertEquals(10, res.attempts());
        assertTrue(res.made() >= 0 && res.made() <= 10);
        long assisted = res.events().stream().filter(TwoPointShotEvent::assisted).count();
        System.out.println("[2PT simulateShots] attempts=10, made=" + res.made() + ", assistedShots=" + assisted);
        assertTrue(assisted > 0, "Avec assistedShotPercentage élevé on devrait voir des passes");
    }

    @Test
    void computeTwoPointPct_shouldVaryWithTypeAdvantageAndAssist() {
        TwoPointSimulator sim = new TwoPointSimulator(new Random(1));
        Player p = new PlayerRandomFactory(42L).random(1, "P");

        double noAssist = sim.computeTwoPointPct( p, 0.0, 0.0);
        double withAssist = sim.computeTwoPointPct( p, 0.0, 0.05);
        double withAdv = sim.computeTwoPointPct( p, 20.0, 0.0);

        System.out.println("[2PT pct] base=" + noAssist + ", assist=" + withAssist + ", adv20=" + withAdv);
        assertTrue(withAssist >= noAssist);
        assertTrue(withAdv >= noAssist);

        double post = sim.computeTwoPointPct( p, 0.0, 0.0);
        System.out.println("[2PT pct] postType=" + post);
        assertTrue(post >= 0.10 && post <= 0.85);
    }

    @Test
    void computeAdvantage2pt_shouldReturnReasonableRange() {
        TwoPointSimulator sim = new TwoPointSimulator(new Random());
        PlayerRandomFactory f = new PlayerRandomFactory();
        Player off = f.random(1, "OFF");
        Player def = f.random(2, "DEF");

        double advMid = sim.computeAdvantage2pt( off, def);
        double advPost = sim.computeAdvantage2pt( off, def);
        System.out.println("[2PT advantage] mid=" + advMid + ", post=" + advPost);
        assertTrue(advMid >= -100 && advMid <= 100);
        assertTrue(advPost >= -100 && advPost <= 100);
    }

    @Test
    void sampleTwoPointAttempts_shouldBeClampedAndResponsive() {
        TwoPointSimulator sim = new TwoPointSimulator(new Random(3));

        int low = sim.sampleTwoPointAttempts(10, 10, 0, 15);
        int high = sim.sampleTwoPointAttempts( 30, 100, 0, 15);

        System.out.println("[2PT attempts] low=" + low + ", high=" + high);
        assertTrue(low >= 0 && low <= 15);
        assertTrue(high >= 0 && high <= 15);
    }

}
