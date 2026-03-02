package com.sanguiwara.badges;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.type.ShotType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BadgeEngineTest {

    private static Player basePlayerWithBadges(Set<Long> badgeIds) {
        int v = 50;
        return Player.builder()
                .id(UUID.randomUUID())
                .name("P")
                .birthDate(1990)
                .injured(false)
                .badgeIds(badgeIds)
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

    private static Stream<Arguments> allBadges_applyCases() {
        double shotPct = 0.30;
        double score = 10.0;
        return Stream.of(
                Arguments.of("Three Point Specialist", Set.of(BadgeCatalog.THREE_POINT_SPECIALIST_ID), BadgeType.THREE_POINT, Target.SHOT_PCT, shotPct, ShotContext.empty(), 0.33),
                Arguments.of("Two Point Specialist", Set.of(BadgeCatalog.TWO_POINT_SPECIALIST_ID), BadgeType.TWO_POINT, Target.SHOT_PCT, shotPct, ShotContext.empty(), 0.32),
                Arguments.of("Drive Finisher", Set.of(BadgeCatalog.DRIVE_FINISHER_ID), BadgeType.DRIVE, Target.SHOT_PCT, shotPct, ShotContext.empty(), 0.32),

                Arguments.of("Rebound Hunter", Set.of(BadgeCatalog.REBOUND_HUNTER_ID), BadgeType.REBOUND, Target.REBOUND_SCORE, score, ReboundContext.offensive(), 11.0),
                Arguments.of("Thief", Set.of(BadgeCatalog.THIEF_ID), BadgeType.STEAL, Target.STEAL_SCORE, score, ShotContext.empty(), 11.0),
                Arguments.of("Playmaker", Set.of(BadgeCatalog.PLAYMAKER_ID), BadgeType.ASSIST, Target.PLAYMAKING_CONTRIBUTION, score, ShotContext.empty(), 11.0),

                Arguments.of("Defensive Rebound Specialist (defensive)", Set.of(BadgeCatalog.DEF_REBOUND_SPECIALIST_ID), BadgeType.REBOUND, Target.REBOUND_SCORE, score, ReboundContext.defensive(), 12.0),

                Arguments.of("Assisted Shot Boost (3pt assisted)", Set.of(BadgeCatalog.ASSISTED_SHOT_BOOST_ID), BadgeType.THREE_POINT, Target.SHOT_PCT, shotPct, ShotContext.forShot(ShotType.THREE_POINT, true, 0.0), 0.32),
                Arguments.of("Assisted Shot Boost (2pt assisted)", Set.of(BadgeCatalog.ASSISTED_SHOT_BOOST_ID), BadgeType.TWO_POINT, Target.SHOT_PCT, shotPct, ShotContext.forShot(ShotType.TWO_POINT, true, 0.0), 0.32),
                Arguments.of("Assisted Shot Boost (drive assisted)", Set.of(BadgeCatalog.ASSISTED_SHOT_BOOST_ID), BadgeType.DRIVE, Target.SHOT_PCT, shotPct, ShotContext.forShot(ShotType.DRIVE, true, 0.0), 0.32),

                Arguments.of("Crazy Shooter (3pt)", Set.of(BadgeCatalog.CRAZY_SHOOTER_ID), BadgeType.THREE_POINT, Target.SHOT_PCT, shotPct, ShotContext.empty(), 0.32),
                Arguments.of("Crazy Shooter (drive)", Set.of(BadgeCatalog.CRAZY_SHOOTER_ID), BadgeType.DRIVE, Target.SHOT_PCT, shotPct, ShotContext.empty(), 0.32)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allBadges_applyCases")
    void apply_appliesAllBadges(String name, Set<Long> badgeIds, BadgeType badgeType, Target target, double baseValue, Context context, double expected) {
        BadgeEngine engine = new BadgeEngine();
        Player p = basePlayerWithBadges(badgeIds);

        double v = engine.apply(p, badgeType, target, baseValue, context);
        assertEquals(expected, v, 1e-9);
    }

    private static Stream<Arguments> contextualBadges_noOpCases() {
        double shotPct = 0.30;
        double score = 10.0;
        return Stream.of(
                Arguments.of("Defensive Rebound Specialist does nothing on offensive rebounds", Set.of(BadgeCatalog.DEF_REBOUND_SPECIALIST_ID), BadgeType.REBOUND, Target.REBOUND_SCORE, score, ReboundContext.offensive(), score),
                Arguments.of("Defensive Rebound Specialist does nothing without rebound context", Set.of(BadgeCatalog.DEF_REBOUND_SPECIALIST_ID), BadgeType.REBOUND, Target.REBOUND_SCORE, score, ShotContext.empty(), score),
                Arguments.of("Assisted Shot Boost does nothing on unassisted shots", Set.of(BadgeCatalog.ASSISTED_SHOT_BOOST_ID), BadgeType.THREE_POINT, Target.SHOT_PCT, shotPct, ShotContext.forShot(ShotType.THREE_POINT, false, 0.0), shotPct),
                Arguments.of("Assisted Shot Boost does nothing without shot context", Set.of(BadgeCatalog.ASSISTED_SHOT_BOOST_ID), BadgeType.THREE_POINT, Target.SHOT_PCT, shotPct, ReboundContext.defensive(), shotPct)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("contextualBadges_noOpCases")
    void apply_doesNotApplyContextualBadgesWhenContextDoesNotMatch(String name, Set<Long> badgeIds, BadgeType badgeType, Target target, double baseValue, Context context, double expected) {
        BadgeEngine engine = new BadgeEngine();
        Player p = basePlayerWithBadges(badgeIds);

        double v = engine.apply(p, badgeType, target, baseValue, context);
        assertEquals(expected, v, 1e-9);
    }

    @Test
    void apply_appliesMultipleBadgesSequentially() {
        BadgeEngine engine = new BadgeEngine();
        Player p = basePlayerWithBadges(Set.of(
                BadgeCatalog.THREE_POINT_SPECIALIST_ID,
                BadgeCatalog.TWO_POINT_SPECIALIST_ID
        ));

        double v = engine.apply(p, BadgeType.THREE_POINT, Target.SHOT_PCT, 0.30, ShotContext.empty());
        assertEquals(0.33, v, 1e-9);

        double v2 = engine.apply(p, BadgeType.TWO_POINT, Target.SHOT_PCT, 0.30, ShotContext.empty());
        assertEquals(0.32, v2, 1e-9);
    }

    @Test
    void apply_stacksAdditiveShotBadges() {
        BadgeEngine engine = new BadgeEngine();
        Player p = basePlayerWithBadges(Set.of(
                BadgeCatalog.THREE_POINT_SPECIALIST_ID,
                BadgeCatalog.ASSISTED_SHOT_BOOST_ID,
                BadgeCatalog.CRAZY_SHOOTER_ID
        ));

        double v = engine.apply(
                p,
                BadgeType.THREE_POINT,
                Target.SHOT_PCT,
                0.30,
                ShotContext.forShot(ShotType.THREE_POINT, true, 0.0)
        );
        assertEquals(0.37, v, 1e-9);
    }
}
