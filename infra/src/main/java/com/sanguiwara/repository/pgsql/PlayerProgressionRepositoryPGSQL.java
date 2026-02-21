package com.sanguiwara.repository.pgsql;

import com.sanguiwara.mapper.PlayerProgressionMapper;
import com.sanguiwara.progression.PlayerProgression;
import com.sanguiwara.progression.ProgressionEventType;
import com.sanguiwara.repository.PlayerProgressionRepository;
import com.sanguiwara.repository.jpa.PlayerProgressionJpaRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PlayerProgressionRepositoryPGSQL implements PlayerProgressionRepository {

    private final PlayerProgressionJpaRepository jpaRepository;
    private final PlayerProgressionMapper mapper;

    @Override
    public @NonNull PlayerProgression save(@NonNull PlayerProgression progression) {
        var saved = jpaRepository.save(mapper.toEntity(progression));
        return mapper.toDomain(saved);
    }

    @Override
    public @NonNull List<PlayerProgression> saveAll(@NonNull List<PlayerProgression> progressions) {
        var saved = jpaRepository.saveAll(progressions.stream().map(mapper::toEntity).toList());
        return saved.stream().map(mapper::toDomain).toList();
    }

    @Override
    public @NonNull List<PlayerProgression> findByEvent(@NonNull ProgressionEventType eventType, @NonNull UUID eventId) {
        return jpaRepository.findAllById_EventTypeAndId_EventId(eventType, eventId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public @NonNull List<PlayerProgression> findByPlayerId(@NonNull UUID playerId) {
        return jpaRepository.findAllByPlayer_Id(playerId).stream().map(mapper::toDomain).toList();
    }
}
