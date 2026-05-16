package com.sanguiwara.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Embeddable
public class PlayerSeasonSnapshotId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "league_season_id", columnDefinition = "uuid", nullable = false)
    private UUID leagueSeasonId;

    @Column(name = "player_id", columnDefinition = "uuid", nullable = false)
    private UUID playerId;

    protected PlayerSeasonSnapshotId() {
    }

    public PlayerSeasonSnapshotId(UUID leagueSeasonId, UUID playerId) {
        this.leagueSeasonId = leagueSeasonId;
        this.playerId = playerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerSeasonSnapshotId other)) return false;
        return Objects.equals(leagueSeasonId, other.leagueSeasonId)
                && Objects.equals(playerId, other.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leagueSeasonId, playerId);
    }
}
