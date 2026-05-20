package com.sanguiwara.modifiers;

import com.sanguiwara.badges.BadgeCatalog;
import com.sanguiwara.badges.Context;
import com.sanguiwara.badges.ModifierType;
import com.sanguiwara.badges.ReboundContext;
import com.sanguiwara.badges.ShotContext;
import com.sanguiwara.badges.Target;
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

class PlayerModifierEngineTest {

    private static long badgeId(String name) {
        return BadgeCatalog.badgeMap().values().stream()
                .filter(badge -> badge.name().equals(name))
                .findFirst()
                .orElseThrow()
                .id();
    }

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
                Arguments.of("Three Point Specialist", Set.of(badgeId("Three Point Specialist")), ModifierType.THREE_POINT, Target.SHOT_PCT, shotPct, ShotContext.empty(), 0.40),
                Arguments.of("Two Point Specialist", Set.of(badgeId("Two Point Specialist")), ModifierType.TWO_POINT, Target.SHOT_PCT, shotPct, ShotContext.empty(), 0.40),
                Arguments.of("Drive Finisher", Set.of(badgeId("Drive Finisher")), ModifierType.DRIVE, Target.SHOT_PCT, shotPct, ShotContext.empty(), 0.40),

                Arguments.of("Rebound Hunter", Set.of(badgeId("Rebound Hunter")), ModifierType.REBOUND, Target.REBOUND_SCORE, score, ReboundContext.offensive(), 11.0),
                Arguments.of("Thief", Set.of(badgeId("Thief")), ModifierType.STEAL, Target.STEAL_SCORE, score, ShotContext.empty(), 11.0),
                Arguments.of("Playmaker", Set.of(badgeId("Playmaker")), ModifierType.ASSIST, Target.PLAYMAKING_CONTRIBUTION, score, ShotContext.empty(), 11.0),

                Arguments.of("Defensive Rebound Specialist (defensive)", Set.of(badgeId("Defensive Rebound Specialist")), ModifierType.REBOUND, Target.REBOUND_SCORE, score, ReboundContext.defensive(), 12.0),

                Arguments.of("Assisted Shot Boost (3pt assisted)", Set.of(badgeId("Assisted Shot Boost")), ModifierType.THREE_POINT, Target.SHOT_PCT, shotPct, ShotContext.forShot(ShotType.THREE_POINT, true, 0.0), 0.32),
                Arguments.of("Assisted Shot Boost (2pt assisted)", Set.of(badgeId("Assisted Shot Boost")), ModifierType.TWO_POINT, Target.SHOT_PCT, shotPct, ShotContext.forShot(ShotType.TWO_POINT, true, 0.0), 0.32),
                Arguments.of("Assisted Shot Boost (drive assisted)", Set.of(badgeId("Assisted Shot Boost")), ModifierType.DRIVE, Target.SHOT_PCT, shotPct, ShotContext.forShot(ShotType.DRIVE, true, 0.0), 0.32),

                Arguments.of("Crazy Shooter (3pt)", Set.of(badgeId("Crazy Shooter")), ModifierType.THREE_POINT, Target.SHOT_PCT, shotPct, ShotContext.empty(), 0.32),
                Arguments.of("Crazy Shooter (drive)", Set.of(badgeId("Crazy Shooter")), ModifierType.DRIVE, Target.SHOT_PCT, shotPct, ShotContext.empty(), 0.32)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allBadges_applyCases")
    void apply_appliesAllBadges(String name, Set<Long> badgeIds, ModifierType effectType, Target target, double baseValue, Context context, double expected) {
        PlayerModifierEngine engine = new PlayerModifierEngine();
        Player p = basePlayerWithBadges(badgeIds);

        double v = engine.apply(p, effectType, target, baseValue, context);
        assertEquals(expected, v, 1e-9);
    }

    private static Stream<Arguments> contextualBadges_noOpCases() {
        double shotPct = 0.30;
        double score = 10.0;
        return Stream.of(
                Arguments.of("Defensive Rebound Specialist does nothing on offensive rebounds", Set.of(badgeId("Defensive Rebound Specialist")), ModifierType.REBOUND, Target.REBOUND_SCORE, score, ReboundContext.offensive(), score),
                Arguments.of("Defensive Rebound Specialist does nothing without rebound context", Set.of(badgeId("Defensive Rebound Specialist")), ModifierType.REBOUND, Target.REBOUND_SCORE, score, ShotContext.empty(), score),
                Arguments.of("Assisted Shot Boost does nothing on unassisted shots", Set.of(badgeId("Assisted Shot Boost")), ModifierType.THREE_POINT, Target.SHOT_PCT, shotPct, ShotContext.forShot(ShotType.THREE_POINT, false, 0.0), shotPct),
                Arguments.of("Assisted Shot Boost does nothing without shot context", Set.of(badgeId("Assisted Shot Boost")), ModifierType.THREE_POINT, Target.SHOT_PCT, shotPct, ReboundContext.defensive(), shotPct)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("contextualBadges_noOpCases")
    void apply_doesNotApplyContextualBadgesWhenContextDoesNotMatch(String name, Set<Long> badgeIds, ModifierType effectType, Target target, double baseValue, Context context, double expected) {
        PlayerModifierEngine engine = new PlayerModifierEngine();
        Player p = basePlayerWithBadges(badgeIds);

        double v = engine.apply(p, effectType, target, baseValue, context);
        assertEquals(expected, v, 1e-9);
    }

    @Test
    void apply_appliesMultipleBadgesSequentially() {
        PlayerModifierEngine engine = new PlayerModifierEngine();
        Player p = basePlayerWithBadges(Set.of(
                badgeId("Three Point Specialist"),
                badgeId("Two Point Specialist")
        ));

        double v = engine.apply(p, ModifierType.THREE_POINT, Target.SHOT_PCT, 0.30, ShotContext.empty());
        assertEquals(0.40, v, 1e-9);

        double v2 = engine.apply(p, ModifierType.TWO_POINT, Target.SHOT_PCT, 0.30, ShotContext.empty());
        assertEquals(0.40, v2, 1e-9);
    }

    @Test
    void apply_stacksAdditiveShotBadges() {
        PlayerModifierEngine engine = new PlayerModifierEngine();
        Player p = basePlayerWithBadges(Set.of(
                badgeId("Three Point Specialist"),
                badgeId("Assisted Shot Boost"),
                badgeId("Crazy Shooter")
        ));

        double v = engine.apply(
                p,
                ModifierType.THREE_POINT,
                Target.SHOT_PCT,
                0.30,
                ShotContext.forShot(ShotType.THREE_POINT, true, 0.0)
        );
        assertEquals(0.44, v, 1e-9);
    }

    @Test
    void apply_appliesTemporaryModifierOnlyOnMatchingTypeAndTarget() {
        PlayerModifierEngine engine = new PlayerModifierEngine();
        Player p = basePlayerWithBadges(Set.of());
        p.addTemporaryModifier(PlayerModifier.nextGameThreePointShotPctBonus(0.05));

        double threePoint = engine.apply(p, ModifierType.THREE_POINT, Target.SHOT_PCT, 0.30, ShotContext.empty());
        double twoPoint = engine.apply(p, ModifierType.TWO_POINT, Target.SHOT_PCT, 0.30, ShotContext.empty());
        double threePointDefense = engine.apply(p, ModifierType.THREE_POINT, Target.DEFENSE_SCORE, 10.0, ShotContext.empty());

        assertEquals(0.35, threePoint, 1e-9);
        assertEquals(0.30, twoPoint, 1e-9);
        assertEquals(10.0, threePointDefense, 1e-9);
    }
}
