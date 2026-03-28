package com.sanguiwara.repository.jpa;

import com.sanguiwara.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface TeamJpaRepository extends JpaRepository<TeamEntity, UUID> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update TeamEntity t set t.name = :name where t.id = :id")
    int updateName(@Param("id") UUID id, @Param("name") String name);
}
