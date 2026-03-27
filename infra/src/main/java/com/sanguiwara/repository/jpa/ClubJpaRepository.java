package com.sanguiwara.repository.jpa;

import com.sanguiwara.entity.ClubEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClubJpaRepository extends JpaRepository<ClubEntity, UUID> {
    List<ClubEntity> findAllByUserIsNull();

    Optional<ClubEntity> findByUser_Sub(String sub);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update ClubEntity c set c.name = :name where c.id = :id")
    int updateName(@Param("id") UUID id, @Param("name") String name);
}
