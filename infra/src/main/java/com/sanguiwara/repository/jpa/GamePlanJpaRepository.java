package com.sanguiwara.repository.jpa;

import com.sanguiwara.entity.GamePlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GamePlanJpaRepository extends JpaRepository<GamePlanEntity, UUID> {
}
