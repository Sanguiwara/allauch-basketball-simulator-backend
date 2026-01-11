package com.sanguiwara.repository.pgsql;

import com.sanguiwara.baserecords.Team;
import com.sanguiwara.mapper.TeamMapper;
import com.sanguiwara.repository.TeamRepository;
import com.sanguiwara.repository.jpa.TeamJpaRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TeamRepositoryPGSQL implements TeamRepository {

    private final TeamJpaRepository teamJpaRepository;
    private final TeamMapper teamMapper;

    @Override
    public @NonNull Optional<Team> findById(UUID id) {
        return teamJpaRepository.findById(id).map(teamMapper::toDomain);
    }

    @Override
    public List<Team> findAll() {
        return teamJpaRepository.findAll().stream()
                .map(teamMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Team save(Team team) {
        var entity = teamMapper.toEntity(team);
        var saved = teamJpaRepository.save(entity);
        return teamMapper.toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        teamJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        teamJpaRepository.deleteAll();
    }
}
