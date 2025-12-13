package com.sanguiwara.allauchsimulator.application.player;

import com.sanguiwara.allauchsimulator.domain.player.PlayerRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepositoryPort playerRepository;

    public String getPlayer() {
        return playerRepository.findById(0L)
            .map(p -> p.getName())
            .orElse(null);
    }
}
