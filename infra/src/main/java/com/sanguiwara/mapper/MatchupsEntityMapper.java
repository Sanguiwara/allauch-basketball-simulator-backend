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
        Map<PlayerEntity, PlayerEntity> storedMatchups = new HashMap<>();
        if (matchups == null || matchups.isEmpty()) {
            return storedMatchups;
        }

        // Keep legacy rows readable by persisting the domain order as-is.
        // The SQL column names are misleading, but runtime values remain defender -> attacker.
        matchups.asMap().forEach((defender, attacker) -> storedMatchups.put(
                playerMapper.toEntity(defender.player()),
                playerMapper.toEntity(attacker.player())
        ));
        return storedMatchups;
    }

    public Matchups toDomain(Map<PlayerEntity, PlayerEntity> storedMatchups) {
        Matchups matchups = Matchups.empty();
        if (storedMatchups == null || storedMatchups.isEmpty()) {
            return matchups;
        }

        storedMatchups.forEach((defender, attacker) -> matchups.assign(
                new MatchupDefender(playerMapper.toDomain(defender)),
                new MatchupAttacker(playerMapper.toDomain(attacker))
        ));
        return matchups;
    }
}
