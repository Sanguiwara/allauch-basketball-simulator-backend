package com.sanguiwara.repository.pgsql;

import com.sanguiwara.baserecords.League;
import com.sanguiwara.mapper.LeagueMapper;
import com.sanguiwara.repository.LeagueRepository;
import com.sanguiwara.repository.jpa.LeagueJpaRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class LeagueRepositoryPGSQL implements LeagueRepository {

    private final LeagueJpaRepository leagueJpaRepository;
    private final LeagueMapper leagueMapper;

    @Override
    public @NonNull Optional<League> findById(Long id) {
        return leagueJpaRepository.findById(id).map(leagueMapper::toDomain);
    }

    @Override
    public List<League> findAll() {
        return leagueJpaRepository.findAll().stream()
                .map(leagueMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public League save(League league) {
        var entity = leagueMapper.toEntity(league);
        var saved = leagueJpaRepository.save(entity);
        return leagueMapper.toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        leagueJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        leagueJpaRepository.deleteAll();
    }
}
