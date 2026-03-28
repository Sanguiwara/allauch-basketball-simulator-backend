package com.sanguiwara.repository;

import com.sanguiwara.baserecords.Game;
import com.sanguiwara.baserecords.GameSummary;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameRepository {

    Optional<Game> findById(UUID id);

    List<Game> findAllGamesForAteam(UUID teamId);


    List<Game> findAll();

    /**
     * Read-model optimized for list endpoints.
     * Must not trigger deep graph loading (activePlayers/matchups/positions/players...).
     */
    List<GameSummary> findAllSummaries();

    Game save(Game game);

    void deleteById(Long id);

    void deleteAll();
}
