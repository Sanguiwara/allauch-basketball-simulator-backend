package com.sanguiwara.progression.manager;

import com.sanguiwara.badges.BadgeCatalog;
import com.sanguiwara.badges.BadgeType;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.TrainingType;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingProgressionManagerTest {

    @Test
    void applyTraining_shooting_increasesAllShootingSkillsByOne() {
        TrainingProgressionManager manager = new TrainingProgressionManager(alwaysMinNoDropRandom());
        Player p = basePlayer();

        p.setTir3Pts(50);
        p.setTir2Pts(50);
        p.setLancerFranc(50);
        p.setFloater(50);
        p.setFinitionAuCercle(50);

        manager.applyTraining(TrainingType.SHOOTING, p);

        assertThat(p.getTir3Pts()).isEqualTo(51);
        assertThat(p.getTir2Pts()).isEqualTo(51);
        assertThat(p.getLancerFranc()).isEqualTo(51);
        assertThat(p.getFloater()).isEqualTo(51);
        assertThat(p.getFinitionAuCercle()).isEqualTo(51);
    }

    @Test
    void applyTraining_tactical_canRollUpToThreePerSkill() {
        TrainingProgressionManager manager = new TrainingProgressionManager(alwaysMaxNoDropRandom());
        Player p = basePlayer();

        p.setBasketballIqOff(50);
        p.setBasketballIqDef(50);
        p.setIq(50);

        manager.applyTraining(TrainingType.TACTICAL, p);

        assertThat(p.getBasketballIqOff()).isEqualTo(53);
        assertThat(p.getBasketballIqDef()).isEqualTo(53);
        assertThat(p.getIq()).isEqualTo(53);
    }

    @Test
    void applyTraining_physical_canRollUpToThreePerSkill() {
        TrainingProgressionManager manager = new TrainingProgressionManager(alwaysMaxNoDropRandom());
        Player p = basePlayer();

        p.setPhysique(50);
        p.setSpeed(50);
        p.setEndurance(50);
        p.setSolidite(50);

        manager.applyTraining(TrainingType.PHYSICAL, p);

        assertThat(p.getPhysique()).isEqualTo(53);
        assertThat(p.getSpeed()).isEqualTo(53);
        assertThat(p.getEndurance()).isEqualTo(53);
        assertThat(p.getSolidite()).isEqualTo(53);
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
        return Player.builder()
                .teamsID(new HashSet<>())
                .clubID(UUID.randomUUID())
                .badgeIds(new HashSet<>())
                .id(UUID.randomUUID())
                .name("p")
                .birthDate(2000)
                .injured(false)
                .build();
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

    private static Random alwaysMinNoDropRandom() {
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

    private static Random alwaysMaxNoDropRandom() {
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
}
