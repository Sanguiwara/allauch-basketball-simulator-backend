package com.sanguiwara.repository;

import com.sanguiwara.baserecords.Game;

import java.util.Optional;
import java.util.UUID;

public interface GameRepository {

    Optional<Game> findById(UUID id);


    Game save(Game game);

    void deleteById(Long id);

    void deleteAll();
}
