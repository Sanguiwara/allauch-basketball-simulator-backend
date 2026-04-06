package com.sanguiwara.repository.pgsql;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.entity.BadgeEntity;
import com.sanguiwara.entity.PlayerEntity;
import com.sanguiwara.entity.TeamEntity;
import com.sanguiwara.mapper.PlayerMapper;
import com.sanguiwara.repository.PlayerRepository;
import com.sanguiwara.repository.jpa.BadgeJpaRepository;
import com.sanguiwara.repository.jpa.PlayerJpaRepository;
import com.sanguiwara.repository.jpa.TeamJpaRepository;
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
    private final TeamJpaRepository teamJpaRepository;
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

        // Sync join table (player_badges) without "clear + re-add everything". Clearing forces Hibernate to delete
        // and re-insert all links, which can trigger duplicate PK errors depending on flush ordering.
        syncBadges(player, entity);

        // Sync join table (team_players) using managed TeamEntity references to avoid transient instance issues.
        syncTeams(player, entity);

        var savedEntity = playerJpaRepository.save(entity);
        return playerMapper.toDomain(savedEntity);
    }

    private void syncBadges(Player player, PlayerEntity playerEntity) {
        Objects.requireNonNull(playerEntity, "playerEntity");

        Set<Long> desiredIds =  player.getBadgeIds();
        if (player.getBadgeIds().isEmpty()) {
            playerEntity.getBadges().clear();
            return;
        }

        List<BadgeEntity> managedBadges = badgeJpaRepository.findAllById(desiredIds);
        playerEntity.getBadges().removeIf(badge ->  !managedBadges.contains(badge));



        managedBadges.forEach(badge -> {
            if (playerEntity.getBadges().contains(badge)) return; // already linked
            playerEntity.getBadges().add(badge); // managed instance
        });

    }

    private void syncTeams(Player player, PlayerEntity playerEntity) {
        Objects.requireNonNull(playerEntity, "playerEntity");

        Set<UUID> desiredIds = player.getTeamsID();
        if (desiredIds == null || desiredIds.isEmpty()) {
            playerEntity.getTeams().clear();
            return;
        }

        List<TeamEntity> managedTeams = teamJpaRepository.findAllById(desiredIds);
        Set<UUID> managedIds = managedTeams.stream().map(TeamEntity::getId).collect(Collectors.toSet());
        if (managedIds.size() != desiredIds.size()) {
            Set<UUID> missing = new HashSet<>(desiredIds);
            missing.removeAll(managedIds);
            throw new NoSuchElementException("Team(s) not found: " + missing);
        }

        playerEntity.getTeams().removeIf(team -> !managedTeams.contains(team));

        managedTeams.forEach(team -> {
            if (playerEntity.getTeams().contains(team)) return; // already linked
            playerEntity.getTeams().add(team); // managed instance
        });
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
