package com.sanguiwara.repository;

import com.sanguiwara.baserecords.Training;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrainingRepository {
    @NonNull
    Optional<Training> findById(UUID id);

    @NonNull
    List<Training> findAllByTeamId(@NonNull UUID teamId);

    @NonNull
    Optional<Training> findNextByTeamId(@NonNull UUID teamId, @NonNull java.time.Instant fromInclusive);

    @NonNull
    List<Training> findAll();

    @NonNull
    Training save(@NonNull Training training);

    void deleteById(@NonNull UUID id);

    void deleteAll();
}
