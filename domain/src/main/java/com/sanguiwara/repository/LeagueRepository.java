package com.sanguiwara.repository;

import com.sanguiwara.baserecords.League;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface LeagueRepository {
    @NonNull
    Optional<League> findById(Long id);

    List<League> findAll();

    League save(League league);

    void deleteById(Long id);

    void deleteAll();
}
