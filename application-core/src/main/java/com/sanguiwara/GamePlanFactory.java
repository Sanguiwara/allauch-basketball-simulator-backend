package com.sanguiwara;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Team;
import com.sanguiwara.repository.GamePlanRepository;
import lombok.RequiredArgsConstructor;import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GamePlanFactory {

    private final GamePlanRepository gamePlanRepository;

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
