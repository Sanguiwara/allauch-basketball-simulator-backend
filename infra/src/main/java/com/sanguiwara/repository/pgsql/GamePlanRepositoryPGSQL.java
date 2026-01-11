package com.sanguiwara.repository.pgsql;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.mapper.GamePlanMapper;
import com.sanguiwara.repository.GamePlanRepository;
import com.sanguiwara.repository.jpa.GamePlanJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class GamePlanRepositoryPGSQL implements GamePlanRepository {

    private final GamePlanJpaRepository gamePlanJpaRepository;
    private final GamePlanMapper gamePlanMapper;


    @Override
    public Optional<GamePlan> findById(UUID uuid) {
        return gamePlanJpaRepository.findById(uuid).map(gamePlanMapper::toDomain);
    }

    @Override
    public GamePlan save(GamePlan gamePlan) {
        var gamePlanEntity = gamePlanMapper.toEntity(gamePlan);
        var savedEntity = gamePlanJpaRepository.save(gamePlanEntity);
        return gamePlanMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(UUID id) {

    }

    @Override
    public void deleteAll() {

    }
}
