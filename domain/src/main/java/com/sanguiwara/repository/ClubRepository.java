package com.sanguiwara.repository;

import com.sanguiwara.baserecords.Club;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClubRepository {
    @NonNull
    Optional<Club> findById(UUID id);

    List<Club> findAll();

    Club save(Club club);

    void deleteById(UUID id);

    void deleteAll();
}