package com.sanguiwara.service;

import com.sanguiwara.baserecords.Game;
import com.sanguiwara.baserecords.GameSummary;

import java.util.List;
import java.util.UUID;

public interface GameService {


    Game getGameById(UUID gameId);




    List<Game> getAllGamesForATeam(UUID teamId);

    List<GameSummary> getAllGameSummaries();
}
