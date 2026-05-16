package com.sanguiwara.repository.pgsql;

import com.sanguiwara.mapper.PlayerSeasonSnapshotMapper;
import com.sanguiwara.progression.PlayerSeasonSnapshot;
import com.sanguiwara.repository.PlayerSeasonSnapshotRepository;
import com.sanguiwara.repository.jpa.PlayerSeasonSnapshotJpaRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PlayerSeasonSnapshotRepositoryPGSQL implements PlayerSeasonSnapshotRepository {

    private final PlayerSeasonSnapshotJpaRepository jpaRepository;
    private final PlayerSeasonSnapshotMapper mapper;

    @Override
    public @NonNull PlayerSeasonSnapshot save(@NonNull PlayerSeasonSnapshot snapshot) {
        var saved = jpaRepository.save(mapper.toEntity(snapshot));
        return mapper.toDomain(saved);
    }

    @Override
    public @NonNull List<PlayerSeasonSnapshot> saveAll(@NonNull List<PlayerSeasonSnapshot> snapshots) {
        var saved = jpaRepository.saveAll(snapshots.stream().map(mapper::toEntity).toList());
        return saved.stream().map(mapper::toDomain).toList();
    }

    @Override
    public @NonNull List<PlayerSeasonSnapshot> findByPlayerId(@NonNull UUID playerId) {
        return jpaRepository.findById_PlayerId(playerId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
