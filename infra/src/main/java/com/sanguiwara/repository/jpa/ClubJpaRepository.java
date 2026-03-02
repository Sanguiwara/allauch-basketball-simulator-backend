package com.sanguiwara.repository.jpa;

import com.sanguiwara.entity.ClubEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClubJpaRepository extends JpaRepository<ClubEntity, UUID> {
    List<ClubEntity> findAllByUserIsNull();

    Optional<ClubEntity> findByUser_Sub(String sub);
}
