package com.sanguiwara.baserecords;

import com.sanguiwara.result.GameResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Game {

    private final UUID id;
    private final GamePlan homeGamePlan;
    private final GamePlan awayGamePlan;
    private final LeagueSeason leagueSeason;

    @Setter
    private GameResult gameResult;





}
