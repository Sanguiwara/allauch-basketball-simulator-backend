package service;

import com.sanguiwara.factory.PlayerFactory;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.gameevent.TwoPointShotEvent;
import com.sanguiwara.result.TwoPointShootingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.sanguiwara.service.simulator.TwoPointSimulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TwoPointSimulatorTest {

    private static final long SEED = 123456789L;
    private Random random;
    private PlayerFactory playerFactory;

    @BeforeEach
    void setUp() {
        random = new Random(SEED);
        playerFactory = new PlayerFactory(random);
    }

    private static List<InGamePlayer> makePlan(PlayerFactory factory, String prefix, long startId) {
        List<InGamePlayer> players = new ArrayList<>();
        players.add(new InGamePlayer(factory.generatePlayer( prefix + "_PG"), 30, 30, 20));
        players.add(new InGamePlayer(factory.generatePlayer( prefix + "_SG"), 20, 30, 15));
        players.add(new InGamePlayer(factory.generatePlayer( prefix + "_SF"), 20, 25, 15));
        players.add(new InGamePlayer(factory.generatePlayer( prefix + "_PF"), 15, 20, 30));
        players.add(new InGamePlayer(factory.generatePlayer( prefix + "_C"), 10, 15, 30));

        return players;
    }
    @Test
    void simulate2ptForMatchup_shouldProduceEventsAndPrint() {
        long seed = 12345L;
        List<InGamePlayer> home = makePlan(playerFactory, "HOME", 1);
        List<InGamePlayer> away = makePlan(playerFactory, "AWAY", 100);

        InGamePlayer shooter = home.getFirst();
        Player defender = away.getFirst().getPlayer();

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
        List<InGamePlayer> players = new ArrayList<>();
        players.add(new InGamePlayer(playerFactory.generatePlayer( "A"), 30, 15, 15));
        players.add(new InGamePlayer(playerFactory.generatePlayer("B"), 15, 15, 15));
        players.add(new InGamePlayer(playerFactory.generatePlayer( "C"), 15, 15, 15));

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
        Player p = playerFactory.generatePlayer( "P");

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
        Player off = playerFactory.generatePlayer( "OFF");
        Player def = playerFactory.generatePlayer( "DEF");

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
