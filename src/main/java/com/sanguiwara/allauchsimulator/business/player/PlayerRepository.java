package com.sanguiwara.allauchsimulator.business.player;

import lombok.NonNull;
import java.util.Optional;

// Legacy placeholder – no longer a Spring Data repository.
public interface PlayerRepository {
    @NonNull
    Optional<Player> findById(Long id);
}
