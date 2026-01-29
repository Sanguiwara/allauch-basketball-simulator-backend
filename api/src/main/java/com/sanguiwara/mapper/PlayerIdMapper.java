package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.service.PlayerService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PlayerIdMapper {
    private final PlayerService playerService;

    public PlayerIdMapper(PlayerService playerService) {
        this.playerService = playerService;
    }

    public UUID playerToUuid(Player p) {
        return p == null ? null : p.getId();
    }

    public Player uuidToPlayer(UUID id) {
        return id == null ? null : playerService.getPlayer(id);
    }
}