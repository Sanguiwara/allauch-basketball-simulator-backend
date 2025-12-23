package com.sanguiwara.service;

import baserecords.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sanguiwara.repository.PlayerRepository;
import service.PlayerService;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;



    @Override
    public Player getPlayer(Long id) {
        return playerRepository.findById(0L).orElse(null);

    }
}
