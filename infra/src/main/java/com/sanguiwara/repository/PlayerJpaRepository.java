package com.sanguiwara.repository;

import com.sanguiwara.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerJpaRepository extends JpaRepository<PlayerEntity, Long> {
}
