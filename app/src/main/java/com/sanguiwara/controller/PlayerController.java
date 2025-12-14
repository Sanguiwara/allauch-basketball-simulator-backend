package com.sanguiwara.controller;

import core.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import service.PlayerService;

@CrossOrigin(origins = "http://localhost:4200")

@RestController
@RequiredArgsConstructor
public class PlayerController
{
    private final PlayerService playerService;

    @GetMapping("/player/{id}")
    public Player getPlayer(@PathVariable Long id) {
        return playerService.getPlayer(id);
    }
}