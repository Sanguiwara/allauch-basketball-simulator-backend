package com.sanguiwara.allauchsimulator.domain.player;

import java.util.Optional;

public interface PlayerRepositoryPort {
    Optional<Player> findById(Long id);
}
