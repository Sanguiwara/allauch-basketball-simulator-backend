package com.sanguiwara.configuration;

import com.sanguiwara.badges.Badge;
import com.sanguiwara.badges.BadgeCatalog;
import com.sanguiwara.entity.BadgeEntity;
import com.sanguiwara.repository.jpa.BadgeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class BadgeCatalogSeeder {

    private final BadgeJpaRepository badgeJpaRepository;

    @Bean
    @Order(0)
    ApplicationRunner updateBadgeDatabase() {
        return _ -> {
            var badgeMap = BadgeCatalog.badgeMap();
            Set<Long> ids = new HashSet<>(badgeMap.keySet());
            Map<Long, BadgeEntity> existingById = badgeJpaRepository.findAllById(ids).stream()
                    .collect(Collectors.toMap(BadgeEntity::getId, Function.identity()));

            for (Badge badge : badgeMap.values()) {
                BadgeEntity existing = existingById.get(badge.id());
                if (existing == null) {
                    BadgeEntity e = new BadgeEntity();
                    e.setId(badge.id());
                    e.setName(badge.name());
                    e.setDropRate(badge.dropRate());
                    badgeJpaRepository.save(e);
                    continue;
                }

                boolean changed = false;
                if (!badge.name().equals(existing.getName())) {
                    existing.setName(badge.name());
                    changed = true;
                }
                if (Double.compare(badge.dropRate(), existing.getDropRate()) != 0) {
                    existing.setDropRate(badge.dropRate());
                    changed = true;
                }
                if (changed) badgeJpaRepository.save(existing);
            }
        };
    }
}
