package com.sanguiwara.repository;

import com.sanguiwara.baserecords.Game;
import com.sanguiwara.timeevent.GameTimeEvent;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface GameTimeEventRepository {

    Optional<GameTimeEvent> findById(UUID id);

    GameTimeEvent save(GameTimeEvent event);

    Collection<GameTimeEvent> findAll();

    Optional<Game> findNextUpcomingGameForClub(UUID clubId);

    void deleteById(UUID id);

    void deleteAll();
}
