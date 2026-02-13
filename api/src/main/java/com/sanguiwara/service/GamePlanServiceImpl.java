package com.sanguiwara.service;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.repository.GamePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class GamePlanServiceImpl implements GamePlanService {

    private final GamePlanRepository gamePlanRepository;


    @Override
    public Optional<GamePlan> getGamePlan(UUID id) {
        return gamePlanRepository.findById(id);

    }

    @Override
    public Optional<GamePlan> getNextUpcomingGamePlanForClub(UUID clubId) {
        return gamePlanRepository.findNextUpcomingGamePlanForClub(clubId);
    }


    @Override
    public GamePlan update(GamePlan gamePlan) {
        return gamePlanRepository.update(gamePlan);
    }


    @Override
    public void delete(UUID id) {
        gamePlanRepository.deleteById(id);
    }


}
