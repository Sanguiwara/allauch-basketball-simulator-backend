package com.sanguiwara.progression.manager;

import com.sanguiwara.badges.BadgeCatalog;
import com.sanguiwara.badges.BadgeType;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.factory.PlayerArchetype;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TrainingProgressionManagerTest {

    @Test
    void applyTraining_shooting_usesSkillCurveAtMidSkill() {
        TrainingProgressionManager manager = new TrainingProgressionManager(minDeltaHighVarianceNoDropRandom());
        Player p = basePlayer();

        p.setTir3Pts(50);
        p.setTir2Pts(50);
        p.setLancerFranc(50);
        p.setFloater(50);
        p.setFinitionAuCercle(50);

        manager.applyTraining(TrainingType.SHOOTING, p);

        assertThat(p.getTir3Pts()).isEqualTo(55);
        assertThat(p.getTir2Pts()).isEqualTo(55);
        assertThat(p.getLancerFranc()).isEqualTo(55);
        assertThat(p.getFloater()).isEqualTo(55);
        assertThat(p.getFinitionAuCercle()).isEqualTo(55);
    }

    @Test
    void applyTraining_tactical_usesSkillCurveAtMidSkill() {
        TrainingProgressionManager manager = new TrainingProgressionManager(maxDeltaHighVarianceNoDropRandom());
        Player p = basePlayer();

        p.setBasketballIqOff(50);
        p.setBasketballIqDef(50);
        p.setIq(50);

        manager.applyTraining(TrainingType.TACTICAL, p);

        assertThat(p.getBasketballIqOff()).isEqualTo(57);
        assertThat(p.getBasketballIqDef()).isEqualTo(57);
        assertThat(p.getIq()).isEqualTo(57);
    }

    @Test
    void applyTraining_physical_usesSkillCurveAtMidSkill() {
        TrainingProgressionManager manager = new TrainingProgressionManager(maxDeltaHighVarianceNoDropRandom());
        Player p = basePlayer();

        p.setPhysique(50);
        p.setSpeed(50);
        p.setEndurance(50);
        p.setSolidite(50);

        manager.applyTraining(TrainingType.PHYSICAL, p);

        assertThat(p.getPhysique()).isEqualTo(57);
        assertThat(p.getSpeed()).isEqualTo(57);
        assertThat(p.getEndurance()).isEqualTo(57);
        assertThat(p.getSolidite()).isEqualTo(57);
    }

    @Test
    void applyTraining_lowSkillCanGainTwentyWithStrongArchetypeAndHighRoll() {
        TrainingProgressionManager manager = new TrainingProgressionManager(firstSkillMaxVarianceAndRoundUpNoDropRandom());
        Player p = basePlayer(PlayerArchetype.THREE_POINT_SHOOTER);
        p.setTir3Pts(1);

        manager.applyTraining(TrainingType.SHOOTING, p);

        assertThat(p.getTir3Pts()).isEqualTo(21);
    }

    @Test
    void applyTraining_eliteSkillCanGainAtMostOneWithHighRoll() {
        TrainingProgressionManager manager = new TrainingProgressionManager(alwaysDropRandom());
        Player p = basePlayer(PlayerArchetype.THREE_POINT_SHOOTER);
        p.setTir3Pts(90);

        manager.applyTraining(TrainingType.SHOOTING, p);

        assertThat(p.getTir3Pts()).isEqualTo(91);
    }

    @Test
    void applyTraining_maxSkillDoesNotProgress() {
        TrainingProgressionManager manager = new TrainingProgressionManager(alwaysDropRandom());
        Player p = basePlayer(PlayerArchetype.THREE_POINT_SHOOTER);
        p.setTir3Pts(99);

        manager.applyTraining(TrainingType.SHOOTING, p);

        assertThat(p.getTir3Pts()).isEqualTo(99);
    }

    @Test
    void applyTraining_rejectsSkillOutsideGlobalInvariant() {
        TrainingProgressionManager manager = new TrainingProgressionManager(minDeltaHighVarianceNoDropRandom());
        Player p = basePlayer();
        p.setTir3Pts(0);

        assertThatThrownBy(() -> manager.applyTraining(TrainingType.SHOOTING, p))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Skill value must be between 1 and 99");
    }

    @Test
    void applyTraining_shooting_canUnlockShootingBadges() {
        TrainingProgressionManager manager = new TrainingProgressionManager(alwaysDropRandom());
        Player p = basePlayer();
        p.setBadgeIds(new HashSet<>());

        manager.applyTraining(TrainingType.SHOOTING, p);

        Set<Long> expected = new HashSet<>();
        for (var badge : BadgeCatalog.badgeMap().values()) {
            if (badge.types().contains(BadgeType.THREE_POINT)
                    || badge.types().contains(BadgeType.TWO_POINT)
                    || badge.types().contains(BadgeType.DRIVE)) {
                // Training unlocks by drop rate; auto-skill badges have dropRate=0 and should not unlock here.
                if (badge.dropRate() > 0.0) {
                    expected.add(badge.id());
                }
            }
        }

        assertThat(p.getBadgeIds()).containsAll(expected);
    }

    @Test
    void applyTraining_shooting_usesArchetypeProgressionAffinity() {
        Player shooter = basePlayer(PlayerArchetype.THREE_POINT_SHOOTER);
        Player soldier = basePlayer(PlayerArchetype.SOLDIER);
        shooter.setTir3Pts(50);
        soldier.setTir3Pts(50);

        new TrainingProgressionManager(thresholdThenNoDropRandom(0.75)).applyTraining(TrainingType.SHOOTING, shooter);
        new TrainingProgressionManager(thresholdThenNoDropRandom(0.75)).applyTraining(TrainingType.SHOOTING, soldier);

        assertThat(shooter.getTir3Pts()).isEqualTo(56);
        assertThat(soldier.getTir3Pts()).isEqualTo(53);
    }

    @Test
    void applyTraining_defense_canUnlockStealBadges() {
        TrainingProgressionManager manager = new TrainingProgressionManager(alwaysDropRandom());
        Player p = basePlayer();
        p.setBadgeIds(new HashSet<>());

        manager.applyTraining(TrainingType.DEFENSE, p);

        Set<Long> expected = new HashSet<>();
        for (var badge : BadgeCatalog.badgeMap().values()) {
            if (badge.types().contains(BadgeType.STEAL)) {
                // Training unlocks by drop rate; auto-skill badges have dropRate=0 and should not unlock here.
                if (badge.dropRate() > 0.0) {
                    expected.add(badge.id());
                }
            }
        }

        assertThat(p.getBadgeIds()).containsAll(expected);
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
