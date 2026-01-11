package com.sanguiwara.repository;

import com.sanguiwara.baserecords.LeagueSeason;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface LeagueSeasonRepository {
    @NonNull
    Optional<LeagueSeason> findById(Long id);

    List<LeagueSeason> findAll();

    LeagueSeason save(LeagueSeason leagueSeason);

    void deleteById(Long id);

    void deleteAll();
}
