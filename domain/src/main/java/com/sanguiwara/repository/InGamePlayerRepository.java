package com.sanguiwara.repository;

import com.sanguiwara.baserecords.InGamePlayer;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface InGamePlayerRepository {
    @NonNull
    Optional<InGamePlayer> findById(Long id);

    List<InGamePlayer> findAll();

    InGamePlayer save(InGamePlayer inGamePlayer);

    void deleteById(Long id);

    void deleteAll();
}
