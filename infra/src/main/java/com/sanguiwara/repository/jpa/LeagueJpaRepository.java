package com.sanguiwara.repository.jpa;

import com.sanguiwara.entity.LeagueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeagueJpaRepository extends JpaRepository<LeagueEntity, Long> {
}
