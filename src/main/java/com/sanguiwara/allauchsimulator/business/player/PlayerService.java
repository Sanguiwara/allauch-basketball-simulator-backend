package com.sanguiwara.allauchsimulator.business.player;

import lombok.RequiredArgsConstructor;

// Legacy service retained temporarily without Spring annotations.
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    public String getPlayer() {
        return playerRepository.findById(0L).map(Player::getName).orElse(null);
    }
}
