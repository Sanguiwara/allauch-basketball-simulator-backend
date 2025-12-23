package com.sanguiwara.repository;

import baserecords.Player;
import lombok.NonNull;

import java.util.Optional;

public interface PlayerRepository {
    @NonNull
    Optional<Player> findById(Long id);
}
