package com.sanguiwara.baserecords;

import com.sanguiwara.result.BoxScore;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class Game  {

    private final GamePlan homeGamePlan;
    private final GamePlan awayGamePlan;
    private final UUID id;
    private final Instant executeAt;
    private BoxScore boxScore;



    public Game(UUID id, Instant executeAt, GamePlan homeGamePlan , GamePlan awayGamePlan) {
        this.id = id;
        this.homeGamePlan = homeGamePlan;
        this.awayGamePlan = awayGamePlan;
        this.executeAt = executeAt;
    }


}
