package com.sanguiwara.repository.jpa;

import com.sanguiwara.entity.InGamePlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InGamePlayerJpaRepository extends JpaRepository<InGamePlayerEntity, Long> {
}
