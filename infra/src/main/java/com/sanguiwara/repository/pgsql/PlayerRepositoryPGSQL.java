package com.sanguiwara.repository.pgsql;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.badges.BadgeCatalog;
import com.sanguiwara.entity.BadgeEntity;
import com.sanguiwara.repository.jpa.BadgeJpaRepository;
import com.sanguiwara.repository.jpa.PlayerJpaRepository;
import com.sanguiwara.repository.PlayerRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import com.sanguiwara.mapper.PlayerMapper;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PlayerRepositoryPGSQL implements PlayerRepository {

    private final PlayerJpaRepository playerJpaRepository;
    private final BadgeJpaRepository badgeJpaRepository;
    private final PlayerMapper playerMapper;


    @Override
    public  @NonNull Optional<Player> findById(UUID id) {
        return playerJpaRepository.findById(id).map(playerMapper::toDomain);

    }

    @Override
    public List<Player> findAll() {
        return playerJpaRepository.findAll().stream()
                .map(playerMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Player save(Player player) {
        var entity = playerMapper.toEntity(player);

        // Ensure we attach managed BadgeEntity instances (and enforce FK integrity).
        if (player.getBadgeIds() != null && !player.getBadgeIds().isEmpty()) {
            Set<Long> badgeIds = new HashSet<>(player.getBadgeIds());
            ensureBadgesExist(badgeIds);
            var badges = badgeJpaRepository.findAllById(badgeIds);
            entity.setBadges(new HashSet<>(badges)); // managed instances
        } else {
            entity.setBadges(new HashSet<>());
        }

        var savedEntity = playerJpaRepository.save(entity);
        return playerMapper.toDomain(savedEntity);
    }

    private void ensureBadgesExist(Set<Long> badgeIds) {

        Set<Long> existing = new HashSet<>();
        badgeJpaRepository.findAllById(badgeIds).forEach(b -> existing.add(b.getId()));

        Map<Long, com.sanguiwara.badges.Badge> catalog = BadgeCatalog.badgeMap();
        for (Long id : badgeIds) {
            if (existing.contains(id)) continue;
            var badge = catalog.get(id);
            if (badge == null) {
                throw new IllegalArgumentException("Unknown badge id: " + id);
            }

            BadgeEntity e = new BadgeEntity();
            e.setId(badge.id());
            e.setName(badge.name());
            e.setDropRate(badge.dropRate());
            badgeJpaRepository.save(e);
        }
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
