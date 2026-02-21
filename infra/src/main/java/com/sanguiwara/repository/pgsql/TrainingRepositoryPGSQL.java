package com.sanguiwara.repository.pgsql;

import com.sanguiwara.baserecords.Training;
import com.sanguiwara.mapper.TrainingMapper;
import com.sanguiwara.repository.TrainingRepository;
import com.sanguiwara.repository.jpa.TrainingJpaRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TrainingRepositoryPGSQL implements TrainingRepository {

    private final TrainingJpaRepository jpaRepository;
    private final TrainingMapper mapper;

    @Override
    public @NonNull Optional<Training> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public @NonNull List<Training> findAllByTeamId(@NonNull UUID teamId) {
        return jpaRepository.findAllByTeam_Id(teamId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public @NonNull Optional<Training> findNextByTeamId(@NonNull UUID teamId, @NonNull Instant fromInclusive) {
        return jpaRepository
                .findFirstByTeam_IdAndExecuteAtGreaterThanEqualOrderByExecuteAtAsc(teamId, fromInclusive)
                .map(mapper::toDomain);
    }

    @Override
    public @NonNull List<Training> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public @NonNull Training save(@NonNull Training training) {
        var entity = mapper.toEntity(training);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteById(@NonNull UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }
}
