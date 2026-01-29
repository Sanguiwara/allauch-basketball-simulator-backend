package com.sanguiwara.repository.pgsql;

import com.sanguiwara.baserecords.TeamSeason;
import com.sanguiwara.mapper.TeamSeasonMapper;
import com.sanguiwara.repository.TeamSeasonRepository;
import com.sanguiwara.repository.jpa.TeamSeasonJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TeamSeasonRepositoryPGSQL implements TeamSeasonRepository {
    private final TeamSeasonMapper teamSeasonMapper;
    private final TeamSeasonJpaRepository teamSeasonJpaRepository;
    @PersistenceContext
    private EntityManager em;

    @Override
    public TeamSeason save(TeamSeason teamSeason) {
        var entity = teamSeasonMapper.toEntity(teamSeason, em);
        var saved = teamSeasonJpaRepository.save(entity);
        return teamSeasonMapper.toDomain(saved);
    }
}
