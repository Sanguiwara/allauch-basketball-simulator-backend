package com.sanguiwara.repository.jpa;

import com.sanguiwara.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findBySub(String sub);
}

