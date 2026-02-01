package com.sanguiwara.repository;

import com.sanguiwara.baserecords.Game;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameRepository {

    Optional<Game> findById(UUID id);

    List<Game> findAllGamesForAteam(UUID teamId);


    List<Game> findAll();

    Game save(Game game);

    void deleteById(Long id);

    void deleteAll();
}
