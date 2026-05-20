package com.sanguiwara.progression.training;

import com.sanguiwara.badges.BadgeCatalog;
import com.sanguiwara.badges.ModifierType;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.factory.PlayerArchetype;
import com.sanguiwara.modifiers.PlayerModifier;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TrainingEngineTest {

    @Test
    void applyTraining_shooting_usesSkillCurveAtMidSkill() {
        TrainingEngine engine = new TrainingEngine(minDeltaHighVarianceNoDropRandom());
        Player p = basePlayer();

        p.setTir3Pts(50);
        p.setTir2Pts(50);
        p.setLancerFranc(50);
        p.setFloater(50);
        p.setFinitionAuCercle(50);

        engine.applyTraining(progression(TrainingType.SHOOTING), p);

        assertThat(p.getTir3Pts()).isEqualTo(55);
        assertThat(p.getTir2Pts()).isEqualTo(55);
        assertThat(p.getLancerFranc()).isEqualTo(55);
        assertThat(p.getFloater()).isEqualTo(55);
        assertThat(p.getFinitionAuCercle()).isEqualTo(55);
    }

    @Test
    void applyTraining_tactical_usesSkillCurveAtMidSkill() {
        TrainingEngine engine = new TrainingEngine(maxDeltaHighVarianceNoDropRandom());
        Player p = basePlayer();

        p.setBasketballIqOff(50);
        p.setBasketballIqDef(50);
        p.setIq(50);

        engine.applyTraining(progression(TrainingType.TACTICAL), p);

        assertThat(p.getBasketballIqOff()).isEqualTo(57);
        assertThat(p.getBasketballIqDef()).isEqualTo(57);
        assertThat(p.getIq()).isEqualTo(57);
    }

    @Test
    void applyTraining_physical_usesSkillCurveAtMidSkill() {
        TrainingEngine engine = new TrainingEngine(maxDeltaHighVarianceNoDropRandom());
        Player p = basePlayer();

        p.setPhysique(50);
        p.setSpeed(50);
        p.setEndurance(50);
        p.setSolidite(50);

        engine.applyTraining(progression(TrainingType.PHYSICAL), p);

        assertThat(p.getPhysique()).isEqualTo(57);
        assertThat(p.getSpeed()).isEqualTo(57);
        assertThat(p.getEndurance()).isEqualTo(57);
        assertThat(p.getSolidite()).isEqualTo(57);
    }

    @Test
    void applyTraining_playmaking_usesSkillCurveAtMidSkill() {
        TrainingEngine engine = new TrainingEngine(maxDeltaHighVarianceNoDropRandom());
        Player p = basePlayer();

        p.setBallhandling(50);
        p.setPassingSkills(50);
        p.setBasketballIqOff(50);
        p.setIq(50);

        engine.applyTraining(progression(TrainingType.PLAYMAKING), p);

        assertThat(p.getBallhandling()).isEqualTo(57);
        assertThat(p.getPassingSkills()).isEqualTo(57);
        assertThat(p.getBasketballIqOff()).isEqualTo(57);
        assertThat(p.getIq()).isEqualTo(57);
    }

    @Test
    void applyTraining_freePlay_lightlyImprovesMoraleAttackAndDefense() {
        TrainingEngine engine = new TrainingEngine(maxDeltaHighVarianceNoDropRandom());
        Player p = basePlayer();

        engine.applyTraining(progression(TrainingType.FREE_PLAY), p);

        assertThat(p.getMorale()).isEqualTo(51);

        assertThat(p.getTir3Pts()).isGreaterThan(50);
        assertThat(p.getTir2Pts()).isGreaterThan(50);
        assertThat(p.getFinitionAuCercle()).isGreaterThan(50);
        assertThat(p.getBallhandling()).isGreaterThan(50);
        assertThat(p.getPassingSkills()).isGreaterThan(50);
        assertThat(p.getBasketballIqOff()).isGreaterThan(50);

        assertThat(p.getDefExterieur()).isGreaterThan(50);
        assertThat(p.getDefPoste()).isGreaterThan(50);
        assertThat(p.getProtectionCercle()).isGreaterThan(50);
        assertThat(p.getSteal()).isGreaterThan(50);
        assertThat(p.getBasketballIqDef()).isGreaterThan(50);

        assertThat(p.getPhysique()).isEqualTo(50);
        assertThat(p.getSpeed()).isEqualTo(50);
        assertThat(p.getEndurance()).isEqualTo(50);
        assertThat(p.getSolidite()).isEqualTo(50);
    }

    @Test
    void applyTraining_freePlay_canUnlockAttackAndDefenseBadgesAtReducedRate() {
        assertTrainingCanUnlockBadges(
                TrainingType.FREE_PLAY,
                ModifierType.THREE_POINT,
                ModifierType.TWO_POINT,
                ModifierType.DRIVE,
                ModifierType.ASSIST,
                ModifierType.STEAL,
                ModifierType.BLOCK,
                ModifierType.DEF_EXTER
        );
    }

    @Test
    void applyTraining_threePointFocus_createsNextGameThreePointModifierOnly() {
        TrainingEngine engine = new TrainingEngine(minDeltaHighVarianceNoDropRandom());
        Player p = basePlayer();

        engine.applyTraining(progression(TrainingType.THREE_POINT_FOCUS), p);

        assertThat(p.getTemporaryModifiers())
                .containsExactly(PlayerModifier.nextGameThreePointShotPctBonus(0.05));
        assertThat(p.getTir3Pts()).isEqualTo(50);
        assertThat(p.getTir2Pts()).isEqualTo(50);
        assertThat(p.getMorale()).isEqualTo(50);

        p.consumeTemporaryModifiersForGame();

        assertThat(p.getTemporaryModifiers()).isEmpty();
    }

    @Test
    void applyTraining_lowSkillCanGainTwentyWithStrongArchetypeAndHighRoll() {
        TrainingEngine engine = new TrainingEngine(firstSkillMaxVarianceAndRoundUpNoDropRandom());
        Player p = basePlayer(PlayerArchetype.THREE_POINT_SHOOTER);
        p.setTir3Pts(1);

        engine.applyTraining(progression(TrainingType.SHOOTING), p);

        assertThat(p.getTir3Pts()).isEqualTo(21);
    }

    @Test
    void applyTraining_eliteSkillCanGainAtMostOneWithHighRoll() {
        TrainingEngine engine = new TrainingEngine(alwaysDropRandom());
        Player p = basePlayer(PlayerArchetype.THREE_POINT_SHOOTER);
        p.setTir3Pts(90);

        engine.applyTraining(progression(TrainingType.SHOOTING), p);

        assertThat(p.getTir3Pts()).isEqualTo(91);
    }

    @Test
    void applyTraining_maxSkillDoesNotProgress() {
        TrainingEngine engine = new TrainingEngine(alwaysDropRandom());
        Player p = basePlayer(PlayerArchetype.THREE_POINT_SHOOTER);
        p.setTir3Pts(99);

        engine.applyTraining(progression(TrainingType.SHOOTING), p);

        assertThat(p.getTir3Pts()).isEqualTo(99);
    }

    @Test
    void applyTraining_rejectsSkillOutsideGlobalInvariant() {
        TrainingEngine engine = new TrainingEngine(minDeltaHighVarianceNoDropRandom());
        Player p = basePlayer();
        p.setTir3Pts(0);

        assertThatThrownBy(() -> engine.applyTraining(progression(TrainingType.SHOOTING), p))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Skill value must be between 1 and 99");
    }

    @Test
    void applyTraining_moraleGivesMoreMoraleWhenCurrentMoraleIsLow() {
        TrainingEngine engine = new TrainingEngine(maxDeltaHighVarianceNoDropRandom());
        Player lowMoralePlayer = basePlayer();
        Player highMoralePlayer = basePlayer();
        lowMoralePlayer.setMorale(20);
        highMoralePlayer.setMorale(80);

        engine.applyTraining(progression(TrainingType.MORALE), lowMoralePlayer);
        engine.applyTraining(progression(TrainingType.MORALE), highMoralePlayer);

        int lowMoraleGain = lowMoralePlayer.getMorale() - 20;
        int highMoraleGain = highMoralePlayer.getMorale() - 80;
        assertThat(lowMoraleGain).isGreaterThan(highMoraleGain);
    }

    @Test
    void applyTraining_shooting_canUnlockShootingBadges() {
        assertTrainingCanUnlockBadges(
                TrainingType.SHOOTING,
                ModifierType.THREE_POINT,
                ModifierType.TWO_POINT,
                ModifierType.DRIVE
        );
    }

    @Test
    void applyTraining_shooting_usesArchetypeProgressionAffinity() {
        Player shooter = basePlayer(PlayerArchetype.THREE_POINT_SHOOTER);
        Player soldier = basePlayer(PlayerArchetype.SOLDIER);
        shooter.setTir3Pts(50);
        soldier.setTir3Pts(50);

        new TrainingEngine(thresholdThenNoDropRandom(0.75)).applyTraining(progression(TrainingType.SHOOTING), shooter);
        new TrainingEngine(thresholdThenNoDropRandom(0.75)).applyTraining(progression(TrainingType.SHOOTING), soldier);

        assertThat(shooter.getTir3Pts()).isEqualTo(56);
        assertThat(soldier.getTir3Pts()).isEqualTo(53);
    }

    @Test
    void applyTraining_defense_canUnlockDefenseBadges() {
        assertTrainingCanUnlockBadges(
                TrainingType.DEFENSE,
                ModifierType.STEAL,
                ModifierType.BLOCK,
                ModifierType.DEF_EXTER
        );
    }

    @Test
    void applyTraining_physical_canUnlockPhysicalBadges() {
        assertTrainingCanUnlockBadges(
                TrainingType.PHYSICAL,
                ModifierType.DRIVE,
                ModifierType.REBOUND,
                ModifierType.BLOCK
        );
    }

    @Test
    void applyTraining_playmaking_canUnlockPlaymakingBadges() {
        assertTrainingCanUnlockBadges(
                TrainingType.PLAYMAKING,
                ModifierType.ASSIST,
                ModifierType.DRIVE
        );
    }

    @Test
    void applyTraining_tactical_canUnlockTacticalBadges() {
        assertTrainingCanUnlockBadges(
                TrainingType.TACTICAL,
                ModifierType.ASSIST,
                ModifierType.DEF_EXTER
        );
    }

    private static void assertTrainingCanUnlockBadges(TrainingType trainingType, ModifierType... eligibleTypes) {
        TrainingEngine engine = new TrainingEngine(alwaysDropRandom());
        Player p = basePlayer();
        p.setBadgeIds(new HashSet<>());

        engine.applyTraining(progression(trainingType), p);

        assertThat(p.getBadgeIds()).containsAll(expectedRandomBadgeIdsFor(eligibleTypes));
    }

    private static Set<Long> expectedRandomBadgeIdsFor(ModifierType... eligibleTypes) {
        Set<ModifierType> eligible = Set.of(eligibleTypes);
        Set<Long> expected = new HashSet<>();
        for (var badge : BadgeCatalog.badgeMap().values()) {
            // Training unlocks by drop rate; auto-skill badges have dropRate=0 and should not unlock here.
            if (badge.dropRate() <= 0.0) {
                continue;
            }
            for (ModifierType type : badge.types()) {
                if (!eligible.contains(type)) continue;

                expected.add(badge.id());
                break;
            }
        }
        return expected;
    }

    private static TrainingProgression progression(TrainingType trainingType) {
        return TrainingProgressions.defaultFor(trainingType);
    }

    private static Player basePlayer() {
        return basePlayer(PlayerArchetype.ALL_AROUND);
    }

    private static Player basePlayer(PlayerArchetype archetype) {
        Player player = Player.builder()
                .teamsID(new HashSet<>())
                .clubID(UUID.randomUUID())
                .badgeIds(new HashSet<>())
                .id(UUID.randomUUID())
                .name("p")
                .birthDate(2000)
                .archetype(archetype)
                .injured(false)
                .build();
        initializeTrainableSkills(player, 50);
        return player;
    }

    private static void initializeTrainableSkills(Player player, int value) {
        player.setTir3Pts(value);
        player.setTir2Pts(value);
        player.setLancerFranc(value);
        player.setFloater(value);
        player.setFinitionAuCercle(value);
        player.setDefExterieur(value);
        player.setDefPoste(value);
        player.setProtectionCercle(value);
        player.setSteal(value);
        player.setTimingBlock(value);
        player.setPhysique(value);
        player.setSpeed(value);
        player.setEndurance(value);
        player.setSolidite(value);
        player.setBallhandling(value);
        player.setPassingSkills(value);
        player.setBasketballIqOff(value);
        player.setBasketballIqDef(value);
        player.setIq(value);
        player.setMorale(value);
    }

    private static Random alwaysDropRandom() {
        return new Random(0L) {
            @Override
            public double nextDouble() {
                return 0.0;
            }

            @Override
            public int nextInt(int bound) {
                return bound - 1;
            }
        };
    }

    private static Random minDeltaHighVarianceNoDropRandom() {
        return new Random(0L) {
            @Override
            public double nextDouble() {
                return 1.0;
            }

            @Override
            public int nextInt(int bound) {
                return 0;
            }
        };
    }

    private static Random maxDeltaHighVarianceNoDropRandom() {
        return new Random(0L) {
            @Override
            public double nextDouble() {
                return 1.0;
            }

            @Override
            public int nextInt(int bound) {
                return bound - 1;
            }
        };
    }

    private static Random firstSkillMaxVarianceAndRoundUpNoDropRandom() {
        return new Random(0L) {
            private int doubleCalls;

            @Override
            public double nextDouble() {
                doubleCalls++;
                if (doubleCalls == 1) {
                    return 1.0;
                }
                if (doubleCalls == 2) {
                    return 0.0;
                }
                return 1.0;
            }

            @Override
            public int nextInt(int bound) {
                return bound - 1;
            }
        };
    }

    private static Random thresholdThenNoDropRandom(double threshold) {
        return new Random(0L) {
            private int doubleCalls;

            @Override
            public double nextDouble() {
                doubleCalls++;
                return doubleCalls <= 5 ? threshold : 1.0;
            }

            @Override
            public int nextInt(int bound) {
                return 0;
            }
        };
    }
}
