package com.sanguiwara.repository;

import com.sanguiwara.baserecords.Player;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository {
    @NonNull
    Optional<Player> findById(UUID id);

    List<Player> findAll();

    Player save(Player player);

    void deleteById(UUID id);

    void deleteAll();
}
