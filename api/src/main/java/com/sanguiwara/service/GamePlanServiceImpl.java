package com.sanguiwara.service;

import com.sanguiwara.baserecords.AgeCategory;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.Gender;
import com.sanguiwara.baserecords.Team;
import com.sanguiwara.factory.GamePlanFactory;import com.sanguiwara.repository.GamePlanRepository;import lombok.RequiredArgsConstructor;import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class GamePlanServiceImpl implements GamePlanService {

    private final GamePlanRepository gamePlanRepository;
    private final GamePlanFactory gamePlanFactory;
    private final TeamService teamService;



    @Override public Optional<GamePlan> getGamePlan(UUID id) {
        return gamePlanRepository.findById(id);

    }


    @Override public GamePlan save(GamePlan gamePlan) {
        return gamePlanRepository.save(gamePlan);
    }


    @Override public void delete(UUID id) {
        gamePlanRepository.deleteById(id);
    }

    @Override public GamePlan generateGamePlan()
    {
        Team home = teamService.generateTeam(AgeCategory.SENIOR, Gender.MALE);
        Team away = teamService.generateTeam(AgeCategory.SENIOR, Gender.MALE);

        GamePlan gamePlan = gamePlanFactory.generateGamePlan(home, away);
        save(gamePlan);
        return gamePlan;
    }




}
