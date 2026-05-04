package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.MatchupAttacker;
import com.sanguiwara.baserecords.MatchupDefender;
import com.sanguiwara.baserecords.Matchups;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.entity.PlayerEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MatchupsEntityMapperTest {

    private final PlayerMapper playerMapper = Mappers.getMapper(PlayerMapper.class);
    private final MatchupsEntityMapper mapper = new MatchupsEntityMapper(playerMapper);

    @Test
    void toEntity_keepsDefenderToAttackerDirection() {
        Player defender = player("defender");
        Player attacker = player("attacker");

        Matchups matchups = Matchups.of(Map.of(
                new MatchupDefender(defender), new MatchupAttacker(attacker)
        ));

        Map<PlayerEntity, PlayerEntity> entityMatchups = mapper.toEntity(matchups);

        assertThat(entityMatchups).hasSize(1);
        Map.Entry<PlayerEntity, PlayerEntity> entry = entityMatchups.entrySet().iterator().next();
        assertThat(entry.getKey().getId()).isEqualTo(defender.getId());
        assertThat(entry.getValue().getId()).isEqualTo(attacker.getId());
    }

    @Test
    void toDomain_keepsDefenderToAttackerDirection() {
        PlayerEntity defender = playerEntity("defender");
        PlayerEntity attacker = playerEntity("attacker");
        Player defenderPlayer = playerMapper.toDomain(defender);
        Player attackerPlayer = playerMapper.toDomain(attacker);

        Matchups matchups = mapper.toDomain(Map.of(defender, attacker));

        assertThat(matchups.defenderFor(attackerPlayer)).isEqualTo(defenderPlayer);
        assertThat(matchups.attackerFor(defenderPlayer)).isEqualTo(attackerPlayer);
    }

    private static Player player(String name) {
        return Player.builder()
                .id(UUID.randomUUID())
                .name(name)
                .birthDate(20000101)
                .build();
    }

    private static PlayerEntity playerEntity(String name) {
        PlayerEntity entity = new PlayerEntity();
        entity.setId(UUID.randomUUID());
        entity.setName(name);
        entity.setBirthDate(20000101);
        return entity;
    }
}
