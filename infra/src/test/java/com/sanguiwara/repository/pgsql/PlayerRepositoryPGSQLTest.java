package com.sanguiwara.repository.pgsql;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.entity.BadgeEntity;
import com.sanguiwara.entity.PlayerEntity;
import com.sanguiwara.mapper.PlayerMapper;
import com.sanguiwara.repository.jpa.BadgeJpaRepository;
import com.sanguiwara.repository.jpa.PlayerJpaRepository;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = PlayerRepositoryPGSQLTest.TestApplication.class)
@Import(PlayerRepositoryPGSQLTest.MapperConfig.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:playerrepo;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false",
})
@Transactional
class PlayerRepositoryPGSQLTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = PlayerEntity.class)
    @EnableJpaRepositories(basePackageClasses = {
            PlayerJpaRepository.class,
            BadgeJpaRepository.class
    })
    static class TestApplication {
        // Spring Boot will scan entities from the same package as any @Entity referenced
        // by the JPA repositories (PlayerEntity/BadgeEntity are both in com.sanguiwara.entity).
    }

    @TestConfiguration
    static class MapperConfig {
        @Bean
        PlayerMapper playerMapper() {
            // In tests we don't need the Spring component model, just the mapper implementation.
            return Mappers.getMapper(PlayerMapper.class);
        }

        @Bean
        PlayerRepositoryPGSQL playerRepositoryPGSQL(
                PlayerJpaRepository playerJpaRepository,
                BadgeJpaRepository badgeJpaRepository,
                PlayerMapper playerMapper
        ) {
            return new PlayerRepositoryPGSQL(playerJpaRepository, badgeJpaRepository, playerMapper);
        }
    }

    @Autowired
    private PlayerRepositoryPGSQL playerRepository;

    @Autowired
    private PlayerJpaRepository playerJpaRepository;

    @Autowired
    private BadgeJpaRepository badgeJpaRepository;

    @Test
    void save_doesNotInsertDuplicatePlayerBadgeLinks() {
        long badgeId = 17L;
        BadgeEntity badge = new BadgeEntity();
        badge.setId(badgeId);
        badge.setName("Test badge");
        badge.setDropRate(0.1);
        badgeJpaRepository.save(badge);

        Player player = Player.builder()
                .id(null)
                .name("P")
                .birthDate(20000101)
                .badgeIds(Set.of(badgeId))
                .build();

        Player created = playerRepository.save(player);
        playerRepository.save(created);

        PlayerEntity persisted = playerJpaRepository.findById(created.getId()).orElseThrow();
        assertThat(persisted.getBadges()).extracting(BadgeEntity::getId).containsExactly(badgeId);
    }
}
