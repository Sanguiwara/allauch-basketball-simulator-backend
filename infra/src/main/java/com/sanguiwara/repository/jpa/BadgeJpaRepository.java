package com.sanguiwara.repository.jpa;

import com.sanguiwara.entity.BadgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeJpaRepository extends JpaRepository<BadgeEntity, Long> {
}

