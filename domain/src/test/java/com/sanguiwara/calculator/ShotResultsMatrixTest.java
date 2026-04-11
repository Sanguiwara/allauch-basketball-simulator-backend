package com.sanguiwara.calculator;

import com.sanguiwara.badges.BadgeEngine;
import com.sanguiwara.baserecords.DefenseType;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.MatchupAttacker;
import com.sanguiwara.baserecords.MatchupDefender;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.calculator.spec.DriveSpecification;
import com.sanguiwara.calculator.spec.ShotSpec;
import com.sanguiwara.calculator.spec.ThreePointSpecification;
import com.sanguiwara.calculator.spec.TwoPointSpecification;
import com.sanguiwara.defense.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class ShotResultsMatrixTest {

    private static final Path CSV_OUT =
            Paths.get("domain", "build", "reports", "shot-results-matrix.csv");
    private static final Object CSV_LOCK = new Object();
    private static final String CSV_SEP = ";";

    // BadgeCatalog IDs are stable for persistence (start at 0). We avoid referencing package-private constants here.
    private static final long BADGE_THREE_POINT_SPECIALIST_ID = 0L;
    private static final long BADGE_TWO_POINT_SPECIALIST_ID = 1L;
    private static final long BADGE_DRIVE_FINISHER_ID = 2L;

    private enum ShotKind {
        THREE_POINT,
        TWO_POINT,
        DRIVE
    }

    private enum BadgeMode {
        WITHOUT_BADGE,
        WITH_BADGE
    }

    private enum Caliber {
        VERY_WEAK(30),
        WEAK(45),
        AVERAGE(60),
        GOOD(75),
        VERY_GOOD(99);

        private final int v;

        Caliber(int v) {
            this.v = v;
        }
    }

    private static Stream<Arguments> allCombinations() {
        Stream.Builder<Arguments> b = Stream.builder();
        for (ShotKind shotKind : ShotKind.values()) {
            for (Caliber off : Caliber.values()) {
                for (Caliber def : Caliber.values()) {
                    for (DefenseType defenseType : List.of(
                            DefenseType.MAN_TO_MAN,
                            DefenseType.ZONE_2_1_2,
                            DefenseType.ZONE_2_3,
                            DefenseType.ZONE_3_2
                    )) {
                        for (BadgeMode badgeMode : BadgeMode.values()) {
                            for (boolean assisted : List.of(false, true)) {
                                b.add(Arguments.of(shotKind, off, def, defenseType, badgeMode, assisted));
                            }
                        }
                    }
                }
            }
        }
        return b.build();
    }

    private static Stream<Arguments> threePointCombinations() {
        return allCombinations().filter(args -> args.get()[0] == ShotKind.THREE_POINT);
    }

    private static Stream<Arguments> twoPointCombinations() {
        return allCombinations().filter(args -> args.get()[0] == ShotKind.TWO_POINT);
    }

    private static Stream<Arguments> driveCombinations() {
        return allCombinations().filter(args -> args.get()[0] == ShotKind.DRIVE);
    }

    private static Stream<Arguments> onlyManToMan(Stream<Arguments> source) {
        return source.filter(args -> args.get()[3] == DefenseType.MAN_TO_MAN);
    }

    /**
     * MethodSource helper to run the matrix only for MAN_TO_MAN (useful for local / fast runs).
     * Swap existing @MethodSource to these methods when you want to focus on man-to-man only.
     */
    private static Stream<Arguments> threePointCombinationsManToMan() {
        return onlyManToMan(threePointCombinations());
    }

    private static Stream<Arguments> twoPointCombinationsManToMan() {
        return onlyManToMan(twoPointCombinations());
    }

    private static Stream<Arguments> driveCombinationsManToMan() {
        return onlyManToMan(driveCombinations());
    }

    @ParameterizedTest(name = "{0} off={1} def={2} scheme={3} badge={4} assisted={5}")
    @MethodSource("threePointCombinationsManToMan")
    void simulate100Shots_threePoint(ShotKind shotKind, Caliber offensiveCaliber, Caliber defensiveCaliber, DefenseType defenseType, BadgeMode badgeMode, boolean assisted) {
        simulate100Shots(shotKind, offensiveCaliber, defensiveCaliber, defenseType, badgeMode, assisted);
    }

    @ParameterizedTest(name = "{0} off={1} def={2} scheme={3} badge={4} assisted={5}")
    @MethodSource("twoPointCombinationsManToMan")
    void simulate100Shots_twoPoint(ShotKind shotKind, Caliber offensiveCaliber, Caliber defensiveCaliber, DefenseType defenseType, BadgeMode badgeMode, boolean assisted) {
        simulate100Shots(shotKind, offensiveCaliber, defensiveCaliber, defenseType, badgeMode, assisted);
    }

    @ParameterizedTest(name = "{0} off={1} def={2} scheme={3} badge={4} assisted={5}")
    @MethodSource("driveCombinationsManToMan")
    void simulate100Shots_drive(ShotKind shotKind, Caliber offensiveCaliber, Caliber defensiveCaliber, DefenseType defenseType, BadgeMode badgeMode, boolean assisted) {
        simulate100Shots(shotKind, offensiveCaliber, defensiveCaliber, defenseType, badgeMode, assisted);
    }

    private static void simulate100Shots(
            ShotKind shotKind,
            Caliber offensiveCaliber,
            Caliber defensiveCaliber,
            DefenseType defenseType,
            BadgeMode badgeMode,
            boolean assisted
    ) {
        int attempts = 100;

        BadgeEngine badgeEngine = new BadgeEngine();
        DefenseSchemeResolver schemeResolver = new DefenseSchemeResolver(List.of(
                new RegularMan2ManScheme(badgeEngine),
                new Zone212Scheme(badgeEngine),
                new Zone23Scheme(badgeEngine),
                new Zone32Scheme(badgeEngine)
        ));

        // Build attacker/defender players with "same value everywhere" for the selected caliber.
        Set<Long> attackerBadges = badgeMode == BadgeMode.WITH_BADGE ? Set.of(badgeIdFor(shotKind)) : Set.of();
        Player attacker = playerWithAllStats("ATTACKER", offensiveCaliber.v, attackerBadges);
        Player defender = playerWithAllStats("DEFENDER", defensiveCaliber.v, Set.of());

        // In-game wrappers are required by ShotSpec.computePct.
        InGamePlayer shooter = new InGamePlayer(attacker, UUID.randomUUID());
        shooter.setMinutesPlayed(40);

        // Build defensive gameplan for advantage computation.
        GamePlan defensivePlan = new GamePlan(UUID.randomUUID(), null, null);
        defensivePlan.setDefenseType(defenseType);
        defensivePlan.setActivePlayers(List.of(defenderAsInGame(defender)));

        if (defenseType == DefenseType.MAN_TO_MAN) {
            defensivePlan.getMatchups().assign(new MatchupDefender(defender), new MatchupAttacker(attacker));
        }

        ShotSpec<?, ?> spec = specFor(shotKind, badgeEngine);
        DefensiveScheme scheme = schemeResolver.resolve(defenseType);
        double advantage = scheme.calculateAdvantageForAPlayer(shooter, defensivePlan, spec);

        double pctBeforeMoraleBonus = spec.computePct(shooter, advantage, assisted);
        // Morale bonus is disabled for this test matrix: we want to isolate computePct + scheme advantage.
        // double moraleBonus = moraleBonus(attacker.getMorale());
        // double shotPct = pctBeforeMoraleBonus + moraleBonus;
        double shotPct = pctBeforeMoraleBonus;

        Random random = new Random(seed(shotKind, offensiveCaliber, defensiveCaliber, defenseType, badgeMode, assisted));
        int made = 0;
        for (int i = 0; i < attempts; i++) {
            if (random.nextDouble() < shotPct) {
                made++;
            }
        }

        log.info(
                "shot={} off={}({}) def={}({}) scheme={} badge={} assisted={} adv={} pctBeforeMoraleBonus={} pct={} made={}/{}",
                shotKind,
                offensiveCaliber, offensiveCaliber.v,
                defensiveCaliber, defensiveCaliber.v,
                defenseType,
                badgeMode,
                assisted,
                String.format("%.4f", advantage),
                String.format("%.4f", pctBeforeMoraleBonus),
                String.format("%.4f", shotPct),
                made,
                attempts
        );

        appendCsvRow(
                shotKind,
                offensiveCaliber,
                defensiveCaliber,
                defenseType,
                badgeMode,
                assisted,
                advantage,
                pctBeforeMoraleBonus,
                shotPct,
                made,
                attempts
        );

        assertTrue(made >= 0 && made <= attempts);
    }

    @BeforeAll
    static void initCsv() throws Exception {
        Files.createDirectories(CSV_OUT.getParent());
        Files.deleteIfExists(CSV_OUT);
        Files.writeString(
                CSV_OUT,
                String.join(CSV_SEP,
                        "shotKind",
                        "offCaliber",
                        "offValue",
                        "defCaliber",
                        "defValue",
                        "defenseType",
                        "badgeMode",
                        "assisted",
                        "advantage",
                        "basePct",
                        "finalPct",
                        "made",
                        "attempts"
                ) + "\n",
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    private static void appendCsvRow(
            ShotKind shotKind,
            Caliber off,
            Caliber def,
            DefenseType defenseType,
            BadgeMode badgeMode,
            boolean assisted,
            double advantage,
            double basePct,
            double finalPct,
            int made,
            int attempts
    ) {
        Locale locale = Locale.FRANCE; // Excel FR: decimal comma, column separator ';'
        String row = String.format(
                locale,
                "%s" + CSV_SEP + "%s" + CSV_SEP + "%d" + CSV_SEP + "%s" + CSV_SEP + "%d" + CSV_SEP + "%s" + CSV_SEP + "%s" + CSV_SEP + "%s" + CSV_SEP
                        + "%.6f" + CSV_SEP + "%.6f" + CSV_SEP + "%.6f" + CSV_SEP + "%d" + CSV_SEP + "%d%n",
                shotKind,
                off,
                off.v,
                def,
                def.v,
                defenseType,
                badgeMode,
                assisted,
                advantage,
                basePct,
                finalPct,
                made,
                attempts
        );
        synchronized (CSV_LOCK) {
            try {
                Files.writeString(CSV_OUT, row, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (Exception e) {
                throw new RuntimeException("Failed to write CSV: " + CSV_OUT, e);
            }
        }
    }

    private static long seed(ShotKind shotKind, Caliber off, Caliber def, DefenseType defenseType, BadgeMode badgeMode, boolean assisted) {
        long h = 17;
        h = 31 * h + shotKind.ordinal();
        h = 31 * h + off.ordinal();
        h = 31 * h + def.ordinal();
        h = 31 * h + defenseType.ordinal();
        h = 31 * h + badgeMode.ordinal();
        h = 31 * h + (assisted ? 1 : 0);
        return h;
    }

    private static InGamePlayer defenderAsInGame(Player defender) {
        InGamePlayer p = new InGamePlayer(defender, UUID.randomUUID());
        // Zone defense score uses TOTAL_MINUTES_FOR_TEAM=200, so a single defender must carry full weight.
        p.setMinutesPlayed(200);
        return p;
    }

    private static ShotSpec<?, ?> specFor(ShotKind kind, BadgeEngine badgeEngine) {
        // Random isn't used by computePct / score computations, but the specs require one in ctor.
        Random random = new Random(0L);
        return switch (kind) {
            case THREE_POINT -> new ThreePointSpecification(random, badgeEngine);
            case TWO_POINT -> new TwoPointSpecification(random, badgeEngine);
            case DRIVE -> new DriveSpecification(random, badgeEngine);
        };
    }


//    private static double moraleBonus(int morale) {
//        // Same logic as ShotSimulator.applyMoraleBonus (kept private in prod code).
//        return (morale / 99.0) * 0.40 - 0.20;
//    }

    private static long badgeIdFor(ShotKind kind) {
        return switch (kind) {
            case THREE_POINT -> BADGE_THREE_POINT_SPECIALIST_ID;
            case TWO_POINT -> BADGE_TWO_POINT_SPECIALIST_ID;
            case DRIVE -> BADGE_DRIVE_FINISHER_ID;
        };
    }

    private static Player playerWithAllStats(String name, int v, Set<Long> badgeIds) {
        return Player.builder()
                .id(UUID.randomUUID())
                .name(name)
                .birthDate(1990)
                .injured(false)
                .badgeIds(badgeIds == null || badgeIds.isEmpty() ? null : badgeIds)
                .tir3Pts(v)
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
