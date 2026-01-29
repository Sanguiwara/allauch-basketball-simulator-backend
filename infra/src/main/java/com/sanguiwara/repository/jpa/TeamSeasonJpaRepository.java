package com.sanguiwara.repository.jpa;

import com.sanguiwara.entity.TeamSeasonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TeamSeasonJpaRepository extends JpaRepository<TeamSeasonEntity, UUID> {

}
