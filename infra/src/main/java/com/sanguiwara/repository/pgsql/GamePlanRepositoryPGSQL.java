package com.sanguiwara.repository.pgsql;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.mapper.GamePlanMapper;
import com.sanguiwara.mapper.InGamePlayerMapper;
import com.sanguiwara.repository.GamePlanRepository;
import com.sanguiwara.repository.jpa.GamePlanJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class GamePlanRepositoryPGSQL implements GamePlanRepository {

    private final GamePlanJpaRepository gamePlanJpaRepository;
    private final GamePlanMapper gamePlanMapper;
    private final InGamePlayerMapper inGamePlayerMapper;


    @Override
    public Optional<GamePlan> findById(UUID uuid) {
        return gamePlanJpaRepository.findById(uuid).map(gamePlanMapper::toDomain);
    }

    @Override
    public GamePlan update(GamePlan gamePlan) {
        var gamePlanEntity = gamePlanJpaRepository.findById(gamePlan.getId())
                .orElseThrow();

        // 1) update des champs simples via mapstruct
        gamePlanMapper.updateEntity(gamePlanEntity, gamePlan);

        // 2) replace activePlayers "à la main"
        if(gamePlanEntity.getActivePlayers()!=null)
            gamePlanEntity.getActivePlayers().clear();
        else
            gamePlanEntity.setActivePlayers(new ArrayList<>());

        if (gamePlan.getActivePlayers() != null) {
            for (var activePlayer : gamePlan.getActivePlayers()) {
                var activePlayerEntity = inGamePlayerMapper.toEntity(activePlayer);
                activePlayerEntity.setGamePlan(gamePlanEntity);
                gamePlanEntity.getActivePlayers().add(activePlayerEntity);
            }
        }

        var saved = gamePlanJpaRepository.save(gamePlanEntity);
        return gamePlanMapper.toDomain(saved);
    }

    @Override
    public GamePlan save(GamePlan gamePlan) {
        var entity = gamePlanMapper.toEntity(gamePlan); // ou toEntity simple
        return gamePlanMapper.toDomain(gamePlanJpaRepository.save(entity));
    }

    @Override
    public void deleteById(UUID id) {

    }

    @Override
    public void deleteAll() {

    }
}
