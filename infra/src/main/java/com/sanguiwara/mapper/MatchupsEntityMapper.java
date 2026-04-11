package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.MatchupAttacker;
import com.sanguiwara.baserecords.MatchupDefender;
import com.sanguiwara.baserecords.Matchups;
import com.sanguiwara.entity.PlayerEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MatchupsEntityMapper {

    private final PlayerMapper playerMapper;

    public MatchupsEntityMapper(PlayerMapper playerMapper) {
        this.playerMapper = playerMapper;
    }

    public Map<PlayerEntity, PlayerEntity> toEntity(Matchups matchups) {
        Map<PlayerEntity, PlayerEntity> attackerToDefender = new HashMap<>();
        if (matchups == null || matchups.isEmpty()) {
            return attackerToDefender;
        }

        // Domain/API convention is defender -> attacker, but the current relational schema stores attacker -> defender.
        matchups.asMap().forEach((defender, attacker) -> attackerToDefender.put(
                playerMapper.toEntity(attacker.player()),
                playerMapper.toEntity(defender.player())
        ));
        return attackerToDefender;
    }

    public Matchups toDomain(Map<PlayerEntity, PlayerEntity> attackerToDefender) {
        Matchups matchups = Matchups.empty();
        if (attackerToDefender == null || attackerToDefender.isEmpty()) {
            return matchups;
        }

        attackerToDefender.forEach((attacker, defender) -> matchups.assign(
                new MatchupDefender(playerMapper.toDomain(defender)),
                new MatchupAttacker(playerMapper.toDomain(attacker))
        ));
        return matchups;
    }
}
