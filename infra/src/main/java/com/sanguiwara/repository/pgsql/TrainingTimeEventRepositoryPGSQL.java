package com.sanguiwara.repository.pgsql;

import com.sanguiwara.executor.TrainingExecutor;
import com.sanguiwara.mapper.TrainingTimeEventMapper;
import com.sanguiwara.repository.TrainingTimeEventRepository;
import com.sanguiwara.repository.jpa.TrainingTimeEventJpaRepository;
import com.sanguiwara.timeevent.TrainingTimeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TrainingTimeEventRepositoryPGSQL implements TrainingTimeEventRepository {

    private final TrainingTimeEventJpaRepository jpaRepository;
    private final TrainingTimeEventMapper mapper;
    private final TrainingExecutor trainingExecutor;

    @Override
    public Optional<TrainingTimeEvent> findById(UUID id) {
        return jpaRepository.findById(id).map(e -> mapper.toDomain(e, trainingExecutor));
    }

    @Override
    public TrainingTimeEvent save(TrainingTimeEvent event) {
        var saved = jpaRepository.save(mapper.toEntity(event));
        return mapper.toDomain(saved, trainingExecutor);
    }

    @Override
    public Collection<TrainingTimeEvent> findAll() {
        return jpaRepository.findAll().stream().map(e -> mapper.toDomain(e, trainingExecutor)).toList();
    }

    @Override
    public Optional<TrainingTimeEvent> findByTrainingId(UUID trainingId) {
        return jpaRepository.findByTrainingId(trainingId).map(e -> mapper.toDomain(e, trainingExecutor));
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void deleteByTrainingId(UUID trainingId) {
        jpaRepository.deleteByTrainingId(trainingId);
    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }
}
