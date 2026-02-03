package com.sanguiwara.repository.jpa;

import com.sanguiwara.entity.ClubEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClubJpaRepository extends JpaRepository<ClubEntity, UUID> {
}