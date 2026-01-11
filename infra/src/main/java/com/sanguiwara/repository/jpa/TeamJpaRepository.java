package com.sanguiwara.repository.jpa;

import com.sanguiwara.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TeamJpaRepository extends JpaRepository<TeamEntity, UUID> {
}
