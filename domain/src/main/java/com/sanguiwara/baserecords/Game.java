package com.sanguiwara.baserecords;

import com.sanguiwara.result.GameResult;
import com.sanguiwara.progression.PlayerProgression;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Game {

    private final UUID id;
    private final GamePlan homeGamePlan;
    private final GamePlan awayGamePlan;
    private final LeagueSeason leagueSeason;
    private final Instant executeAt;


    @Setter
    private GameResult gameResult;

    @Setter
    private List<PlayerProgression> playerProgressions = new ArrayList<>();




}
