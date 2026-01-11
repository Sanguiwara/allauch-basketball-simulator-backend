package com.sanguiwara.repository.pgsql;

import com.sanguiwara.baserecords.LeagueSeason;
import com.sanguiwara.mapper.LeagueSeasonMapper;
import com.sanguiwara.repository.LeagueSeasonRepository;
import com.sanguiwara.repository.jpa.LeagueSeasonJpaRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class LeagueSeasonRepositoryPGSQL implements LeagueSeasonRepository {

    private final LeagueSeasonJpaRepository leagueSeasonJpaRepository;
    private final LeagueSeasonMapper leagueSeasonMapper;

    @Override
    public @NonNull Optional<LeagueSeason> findById(Long id) {
        return leagueSeasonJpaRepository.findById(id).map(leagueSeasonMapper::toDomain);
    }

    @Override
    public List<LeagueSeason> findAll() {
        return leagueSeasonJpaRepository.findAll().stream()
                .map(leagueSeasonMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public LeagueSeason save(LeagueSeason leagueSeason) {
        var entity = leagueSeasonMapper.toEntity(leagueSeason);
        var saved = leagueSeasonJpaRepository.save(entity);
        return leagueSeasonMapper.toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        leagueSeasonJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        leagueSeasonJpaRepository.deleteAll();
    }
}
