package com.sanguiwara.progression.manager;

import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.factory.PlayerArchetype;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ArchetypeMatchProgressionManagerTest {

    @Test
    void shootingProgression_usesMatchArchetypeAffinity() {
        Player shooter = basePlayer(PlayerArchetype.THREE_POINT_SHOOTER);
        Player soldier = basePlayer(PlayerArchetype.SOLDIER);

        InGamePlayer shooterGame = inGamePlayer(shooter);
        shooterGame.setThreePointAttempt(15);
        InGamePlayer soldierGame = inGamePlayer(soldier);
        soldierGame.setThreePointAttempt(15);

        ShootingSkillProgressionManager manager = new ShootingSkillProgressionManager(noDropRandom());
        manager.applyShootingSkillProgression(shooterGame);
        manager.applyShootingSkillProgression(soldierGame);

        assertThat(shooter.getTir3Pts()).isEqualTo(58);
        assertThat(soldier.getTir3Pts()).isEqualTo(55);
    }

    @Test
    void reboundingProgression_usesMatchArchetypeAffinity() {
        Player soldier = basePlayer(PlayerArchetype.SOLDIER);
        Player shooter = basePlayer(PlayerArchetype.THREE_POINT_SHOOTER);

        InGamePlayer soldierGame = inGamePlayer(soldier);
        soldierGame.setDefensiveRebounds(5);
        InGamePlayer shooterGame = inGamePlayer(shooter);
        shooterGame.setDefensiveRebounds(5);

        ReboundingProgressionManager manager = new ReboundingProgressionManager(noDropRandom());
        manager.applyReboundingProgression(soldierGame);
        manager.applyReboundingProgression(shooterGame);

        assertThat(soldier.getTimingRebond()).isEqualTo(52);
        assertThat(shooter.getTimingRebond()).isEqualTo(51);
    }

    @Test
    void stocksProgression_usesMatchArchetypeAffinity() {
        Player soldier = basePlayer(PlayerArchetype.SOLDIER);
        Player shooter = basePlayer(PlayerArchetype.THREE_POINT_SHOOTER);

        InGamePlayer soldierGame = inGamePlayer(soldier);
        soldierGame.setSteals(6);
        InGamePlayer shooterGame = inGamePlayer(shooter);
        shooterGame.setSteals(6);

        StocksProgressionManager manager = new StocksProgressionManager(noDropRandom());
        manager.applyStocksProgression(soldierGame);
        manager.applyStocksProgression(shooterGame);

        assertThat(soldier.getSteal()).isEqualTo(55);
        assertThat(shooter.getSteal()).isEqualTo(54);
    }

    private static InGamePlayer inGamePlayer(Player player) {
        InGamePlayer inGamePlayer = new InGamePlayer(player, UUID.randomUUID());
        inGamePlayer.setMinutesPlayed(20);
        return inGamePlayer;
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
        player.setTir3Pts(50);
        player.setTir2Pts(50);
        player.setFinitionAuCercle(50);
        player.setFloater(50);
        player.setTimingRebond(50);
        player.setAgressiviteRebond(50);
        player.setSteal(50);
        player.setTimingBlock(50);
        player.setProtectionCercle(50);
        player.setPotentielSkill(50);
        return player;
    }

    private static Random noDropRandom() {
        return new Random(0L) {
            @Override
            public double nextDouble() {
                return 1.0;
            }
        };
    }
}
