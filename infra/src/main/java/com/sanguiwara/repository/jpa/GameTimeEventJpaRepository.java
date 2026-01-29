package com.sanguiwara.repository.jpa;

import com.sanguiwara.entity.GameTimeEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameTimeEventJpaRepository extends JpaRepository<GameTimeEventEntity, UUID> {
}
