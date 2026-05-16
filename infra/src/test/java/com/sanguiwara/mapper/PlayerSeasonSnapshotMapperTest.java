package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.factory.PlayerArchetype;
import com.sanguiwara.progression.PlayerSeasonSnapshot;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerSeasonSnapshotMapperTest {

    private final PlayerSeasonSnapshotMapper mapper = Mappers.getMapper(PlayerSeasonSnapshotMapper.class);

    @Test
    void mapsSnapshotEntityAndDomain() {
        UUID leagueSeasonId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();
        UUID clubId = UUID.randomUUID();
        Player player = Player.builder()
                .id(playerId)
                .name("Snapshot Player")
                .birthDate(20000101)
                .archetype(PlayerArchetype.DRIVE_SPECIALIST)
                .clubID(clubId)
                .badgeIds(Set.of(1L, 2L))
                .tir3Pts(42)
                .tir2Pts(51)
                .morale(65)
                .build();

        var entity = mapper.toEntity(PlayerSeasonSnapshot.from(leagueSeasonId, player));
        var domain = mapper.toDomain(entity);

        assertThat(entity.getId().getLeagueSeasonId()).isEqualTo(leagueSeasonId);
        assertThat(entity.getId().getPlayerId()).isEqualTo(playerId);
        assertThat(entity.getClub().getId()).isEqualTo(clubId);
        assertThat(entity.getBadgeIds()).containsExactlyInAnyOrder(1L, 2L);
        assertThat(domain.leagueSeasonId()).isEqualTo(leagueSeasonId);
        assertThat(domain.player().getId()).isEqualTo(playerId);
        assertThat(domain.player().getClubID()).isEqualTo(clubId);
        assertThat(domain.player().getTir3Pts()).isEqualTo(42);
        assertThat(domain.player().getTir2Pts()).isEqualTo(51);
        assertThat(domain.player().getMorale()).isEqualTo(65);
        assertThat(domain.player().getBadgeIds()).containsExactlyInAnyOrder(1L, 2L);
    }
}
