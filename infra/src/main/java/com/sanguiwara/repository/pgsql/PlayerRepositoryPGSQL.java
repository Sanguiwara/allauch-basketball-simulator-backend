package com.sanguiwara.repository.pgsql;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.entity.PlayerEntity;
import com.sanguiwara.mapper.PlayerMapper;
import com.sanguiwara.repository.PlayerRepository;
import com.sanguiwara.repository.jpa.BadgeJpaRepository;
import com.sanguiwara.repository.jpa.PlayerJpaRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PlayerRepositoryPGSQL implements PlayerRepository {

    private final PlayerJpaRepository playerJpaRepository;
    private final BadgeJpaRepository badgeJpaRepository;
    private final PlayerMapper playerMapper;


    @Override
    public @NonNull Optional<Player> findById(UUID id) {
        return playerJpaRepository.findById(id).map(playerMapper::toDomain);

    }

    @Override
    public List<Player> findAll() {
        return playerJpaRepository.findAll().stream()
                .map(playerMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Player save(Player player) {
        // Important: avoid merging a brand new detached PlayerEntity every time.
        // For ManyToMany join tables (like player_badges), merge of a detached entity can
        // lead Hibernate to re-insert existing links and hit the PK (player_id, badge_id).
        // Updating a managed entity (loaded from DB) makes the operation idempotent.
        PlayerEntity entity;
        if (player.getId() == null) {
            // New player: let Hibernate generate the UUID (UuidGenerator).
            entity = playerMapper.toEntity(player);
        } else {
            entity = playerJpaRepository.findById(player.getId()).orElse(null);
            if (entity == null) {
                // ID provided but not found: treat as insert of a detached aggregate.
                // (This is still used in some flows.)
                entity = playerMapper.toEntity(player);
            } else {
                playerMapper.updateEntity(player, entity);
            }
        }

        // Ensure we attach managed BadgeEntity instances (and enforce FK integrity).
        if (player.getBadgeIds() != null && !player.getBadgeIds().isEmpty()) {
            Set<Long> badgeIds = new HashSet<>(player.getBadgeIds());
            var badges = badgeJpaRepository.findAllById(badgeIds);
            entity.getBadges().clear();
            entity.getBadges().addAll(badges); // managed instances
        } else {
            entity.getBadges().clear();
        }

        var savedEntity = playerJpaRepository.save(entity);
        return playerMapper.toDomain(savedEntity);
    }


    @Override
    public void deleteById(UUID id) {
        playerJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        playerJpaRepository.deleteAll();
    }


}
