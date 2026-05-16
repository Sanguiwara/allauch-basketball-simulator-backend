package com.sanguiwara.service;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.progression.PlayerSeasonSnapshot;
import com.sanguiwara.progression.PlayerSeasonState;
import com.sanguiwara.repository.PlayerRepository;
import com.sanguiwara.repository.PlayerSeasonSnapshotRepository;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlayerServiceImplTest {

    private final PlayerRepository playerRepository = mock(PlayerRepository.class);
    private final PlayerSeasonSnapshotRepository snapshotRepository = mock(PlayerSeasonSnapshotRepository.class);
    private final PlayerServiceImpl service = new PlayerServiceImpl(playerRepository, snapshotRepository);

    @Test
    void getPlayerSeasonState_comparesFirstSeasonStartSnapshotWithCurrentPlayer() {
        UUID playerId = UUID.randomUUID();
        UUID leagueSeasonId = UUID.randomUUID();
        UUID otherLeagueSeasonId = UUID.randomUUID();
        Player seasonStart = player(playerId, 50, Set.of(1L));
        Player current = player(playerId, 54, Set.of(1L, 2L));

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(current));
        when(snapshotRepository.findByPlayerId(playerId))
                .thenReturn(List.of(
                        PlayerSeasonSnapshot.from(leagueSeasonId, seasonStart),
                        PlayerSeasonSnapshot.from(otherLeagueSeasonId, player(playerId, 60, Set.of()))
                ));

        PlayerSeasonState state = service.getPlayerSeasonState(playerId);

        assertThat(state).isNotNull();
        assertThat(state.leagueSeasonId()).isEqualTo(leagueSeasonId);
        assertThat(state.seasonStart().getTir3Pts()).isEqualTo(50);
        assertThat(state.current().getTir3Pts()).isEqualTo(54);
        assertThat(state.delta().tir3Pts()).isEqualTo(4);
        assertThat(state.delta().badgesAdded()).containsExactly(2L);
        assertThat(state.delta().badgesRemoved()).isEmpty();
    }

    @Test
    void getPlayerSeasonState_returnsNullWhenSnapshotIsMissing() {
        UUID playerId = UUID.randomUUID();

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player(playerId, 50, Set.of())));
        when(snapshotRepository.findByPlayerId(playerId)).thenReturn(List.of());

        assertThat(service.getPlayerSeasonState(playerId)).isNull();
    }

    private Player player(UUID playerId, int tir3Pts, Set<Long> badgeIds) {
        return Player.builder()
                .id(playerId)
                .name("P")
                .birthDate(20000101)
                .teamsID(new HashSet<>())
                .clubID(null)
                .badgeIds(new HashSet<>(badgeIds))
                .injured(false)
                .tir3Pts(tir3Pts)
                .build();
    }
}
