package com.sanguiwara.repository;

import com.sanguiwara.progression.PlayerProgression;
import com.sanguiwara.progression.ProgressionEventType;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface PlayerProgressionRepository {

    @NonNull
    PlayerProgression save(@NonNull PlayerProgression progression);

    @NonNull
    List<PlayerProgression> saveAll(@NonNull List<PlayerProgression> progressions);

    @NonNull
    List<PlayerProgression> findByEvent(@NonNull ProgressionEventType eventType, @NonNull UUID eventId);

    @NonNull
    List<PlayerProgression> findByPlayerId(@NonNull UUID playerId);
}
