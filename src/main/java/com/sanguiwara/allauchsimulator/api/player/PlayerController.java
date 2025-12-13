package com.sanguiwara.allauchsimulator.api.player;

import com.sanguiwara.allauchsimulator.application.player.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")

@RestController
@RequiredArgsConstructor
public class PlayerController
{
    private final PlayerService playerService;

    @GetMapping("/player")
    public String getPlayer() {
        return playerService.getPlayer();
    }
}