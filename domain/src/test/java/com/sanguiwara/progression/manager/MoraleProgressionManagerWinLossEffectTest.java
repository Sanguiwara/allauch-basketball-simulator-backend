package com.sanguiwara.progression.manager;

import com.sanguiwara.baserecords.AgeCategory;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.Gender;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.Team;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MoraleProgressionManagerWinLossEffectTest {

    private final MoraleProgressionManager manager = new MoraleProgressionManager();

    @Test
    void applyWinningEffect_keepsThreePointGainAtNeutralMoraleForMaxEgoPlayer() {
        Player player = playerWithMoraleAndEgo(50, 99);
        GamePlan gamePlan = gamePlanWith(player);

        manager.applyWinningEffect(gamePlan);

        assertThat(player.getMorale()).isEqualTo(53);
    }

    @Test
    void applyWinningEffect_givesMoreMoraleWhenCurrentMoraleIsLow() {
        Player lowMoralePlayer = playerWithMoraleAndEgo(20, 99);
        Player highMoralePlayer = playerWithMoraleAndEgo(80, 99);

        manager.applyWinningEffect(gamePlanWith(lowMoralePlayer));
        manager.applyWinningEffect(gamePlanWith(highMoralePlayer));

        int lowMoraleGain = lowMoralePlayer.getMorale() - 20;
        int highMoraleGain = highMoralePlayer.getMorale() - 80;
        assertThat(lowMoraleGain).isGreaterThan(highMoraleGain);
    }

    @Test
    void applyLosingEffect_keepsThreePointLossAtNeutralMoraleForMaxEgoPlayer() {
        Player player = playerWithMoraleAndEgo(50, 99);
        GamePlan gamePlan = gamePlanWith(player);

        manager.applyLosingEffect(gamePlan);

        assertThat(player.getMorale()).isEqualTo(47);
    }

    @Test
    void applyLosingEffect_removesMoreMoraleWhenCurrentMoraleIsHigh() {
        Player lowMoralePlayer = playerWithMoraleAndEgo(20, 99);
        Player highMoralePlayer = playerWithMoraleAndEgo(80, 99);

        manager.applyLosingEffect(gamePlanWith(lowMoralePlayer));
        manager.applyLosingEffect(gamePlanWith(highMoralePlayer));

        int lowMoraleLoss = 20 - lowMoralePlayer.getMorale();
        int highMoraleLoss = 80 - highMoralePlayer.getMorale();
        assertThat(highMoraleLoss).isGreaterThan(lowMoraleLoss);
    }

    @Test
    void applyDidNotPlayPenalty_removesFiveMoraleOnlyFromRosterPlayersAbsentFromActivePlayers() {
        Player activePlayer = playerWithMoraleAndEgo(50, 50);
        Player inactivePlayer = playerWithMoraleAndEgo(50, 50);
        GamePlan gamePlan = gamePlanWithRoster(activePlayer, inactivePlayer);

        manager.applyDidNotPlayPenalty(gamePlan);

        assertThat(activePlayer.getMorale()).isEqualTo(50);
        assertThat(inactivePlayer.getMorale()).isEqualTo(45);
    }

    @Test
    void applyDidNotPlayPenalty_keepsMoraleAtMinimum() {
        Player activePlayer = playerWithMoraleAndEgo(50, 50);
        Player inactivePlayer = playerWithMoraleAndEgo(3, 50);
        GamePlan gamePlan = gamePlanWithRoster(activePlayer, inactivePlayer);

        manager.applyDidNotPlayPenalty(gamePlan);

        assertThat(inactivePlayer.getMorale()).isEqualTo(1);
    }

    private static Player playerWithMoraleAndEgo(int morale, int ego) {
        return Player.builder()
                .id(UUID.randomUUID())
                .name("Test Player")
                .birthDate(2000)
                .morale(morale)
                .ego(ego)
                .build();
    }

    private static GamePlan gamePlanWith(Player player) {
        GamePlan gamePlan = new GamePlan(UUID.randomUUID(), null, null);
        gamePlan.setActivePlayers(List.of(new InGamePlayer(player, gamePlan.getId())));
        return gamePlan;
    }

    private static GamePlan gamePlanWithRoster(Player activePlayer, Player inactivePlayer) {
        Team team = new Team(UUID.randomUUID(), AgeCategory.U18, Gender.MALE, "team");
        team.setPlayers(List.of(activePlayer, inactivePlayer));
        GamePlan gamePlan = new GamePlan(UUID.randomUUID(), team, null);
        gamePlan.setActivePlayers(List.of(new InGamePlayer(activePlayer, gamePlan.getId())));
        return gamePlan;
    }
}
