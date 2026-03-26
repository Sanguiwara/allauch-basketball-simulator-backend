package com.sanguiwara.service;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Team;
import com.sanguiwara.repository.ClubRepository;
import com.sanguiwara.repository.GamePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class GamePlanServiceImpl implements GamePlanService {

    private final GamePlanRepository gamePlanRepository;
    private final ClubRepository clubRepository;


    @Override
    public Optional<GamePlan> getGamePlan(UUID id) {
        return gamePlanRepository.findById(id);

    }

    @Override
    public Optional<GamePlan> getNextUpcomingGamePlanForClub(UUID clubId) {
        return gamePlanRepository.findNextUpcomingGamePlanForClub(clubId);
    }

    @Override
    public Optional<GamePlan> getNextUpcomingGamePlanForAUserSub(String sub) {
        return clubRepository.findByUserSub(sub)
                .flatMap(club -> getNextUpcomingGamePlanForClub(club.getId()));
    }

    @Override
    public GamePlan update(GamePlan gamePlan) {
        return gamePlanRepository.update(gamePlan);
    }


    @Override
    public GamePlan generateGamePlan(Team t1, Team t2) {


        GamePlan gameplan =  new GamePlan(null, t1, t2);
        gameplan = gamePlanRepository.save(gameplan);
        GamePlan finalHomeGamePlan = gameplan;
        List<InGamePlayer> activePlayers = gameplan.getOwnerTeam().getPlayers().stream()
                .limit(10)
                .map(player -> new InGamePlayer(player, finalHomeGamePlan.getId())) // map Player -> InGamePlayer
                .toList();
        gameplan.setActivePlayers(activePlayers);
        return gamePlanRepository.update(gameplan);


    }


}
