package com.sanguiwara.repository.pgsql;

import com.sanguiwara.baserecords.Game;
import com.sanguiwara.executor.GameExecutor;
import com.sanguiwara.mapper.GameMapper;
import com.sanguiwara.mapper.GameTimeEventMapper;
import com.sanguiwara.repository.GameTimeEventRepository;
import com.sanguiwara.repository.jpa.GameTimeEventJpaRepository;
import com.sanguiwara.timeevent.GameTimeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class GameTimeEventRepositoryPGSQL implements GameTimeEventRepository {

    private final GameTimeEventJpaRepository jpaRepository;
    private final GameTimeEventMapper mapper;
    private final GameExecutor gameExecutor;
    private final GameMapper gameMapper;

    @Override
    public Optional<GameTimeEvent> findById(UUID id) {
        return jpaRepository.findById(id).map(e -> mapper.toDomain(e, gameExecutor));
    }

    @Override
    public GameTimeEvent save(GameTimeEvent event) {
        var entity = mapper.toEntity(event);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved, gameExecutor);
    }

    @Override
    public Collection<GameTimeEvent> findAll() {
        return jpaRepository.findAll().stream().map(e -> mapper.toDomain(e, gameExecutor)).toList();
    }

    @Override
    public Optional<Game> findNextUpcomingGameForClub(UUID clubId) {
        return jpaRepository
                .findNextUpcomingGamesByClubId(clubId, Instant.now(), PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .map(gameMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }
}
