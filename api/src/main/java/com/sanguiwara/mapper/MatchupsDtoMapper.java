package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.MatchupAttacker;
import com.sanguiwara.baserecords.MatchupDefender;
import com.sanguiwara.baserecords.Matchups;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class MatchupsDtoMapper {

    private final PlayerIdMapper playerIdMapper;

    public MatchupsDtoMapper(PlayerIdMapper playerIdMapper) {
        this.playerIdMapper = playerIdMapper;
    }

    public Map<UUID, UUID> toDto(Matchups matchups) {
        Map<UUID, UUID> defenderToAttackerIds = new HashMap<>();
        if (matchups == null || matchups.isEmpty()) {
            return defenderToAttackerIds;
        }

        matchups.asMap().forEach((defender, attacker) -> defenderToAttackerIds.put(
                playerIdMapper.playerToUuid(defender.player()),
                playerIdMapper.playerToUuid(attacker.player())
        ));
        return defenderToAttackerIds;
    }

    public Matchups toDomain(Map<UUID, UUID> defenderToAttackerIds) {
        Matchups matchups = Matchups.empty();
        if (defenderToAttackerIds == null || defenderToAttackerIds.isEmpty()) {
            return matchups;
        }

        defenderToAttackerIds.forEach((defenderId, attackerId) -> matchups.assign(
                new MatchupDefender(playerIdMapper.uuidToPlayer(defenderId)),
                new MatchupAttacker(playerIdMapper.uuidToPlayer(attackerId))
        ));
        return matchups;
    }
}
