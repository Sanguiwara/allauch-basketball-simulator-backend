package com.sanguiwara.repository;

import com.sanguiwara.baserecords.Club;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClubRepository {
    @NonNull
    Optional<Club> findById(UUID id);

    @NonNull
    Optional<Club> findByUserSub(@NonNull String sub);

    List<UUID> findAllIdsWithoutUser();

    List<Club> findAll();

    Club attachUser(UUID clubId, Long userId);

    Club save(Club club);

    void deleteById(UUID id);

    void deleteAll();
}
