package com.sanguiwara.repository;

import com.sanguiwara.baserecords.Team;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamRepository {
    @NonNull
    Optional<Team> findById(UUID id);


    List<Team> findAll();

    Team save(Team team);

    void deleteById(UUID id);

    void deleteAll();
}
