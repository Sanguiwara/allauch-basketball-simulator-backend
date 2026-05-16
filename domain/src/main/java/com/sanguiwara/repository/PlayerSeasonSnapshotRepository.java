package com.sanguiwara.repository;

import com.sanguiwara.progression.PlayerSeasonSnapshot;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface PlayerSeasonSnapshotRepository {

    @NonNull
    PlayerSeasonSnapshot save(@NonNull PlayerSeasonSnapshot snapshot);

    @NonNull
    List<PlayerSeasonSnapshot> saveAll(@NonNull List<PlayerSeasonSnapshot> snapshots);

    @NonNull
    List<PlayerSeasonSnapshot> findByPlayerId(@NonNull UUID playerId);
}
