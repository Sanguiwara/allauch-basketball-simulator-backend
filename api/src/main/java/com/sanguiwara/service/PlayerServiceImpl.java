package com.sanguiwara.service;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;


    @Override
    public Player getPlayer(UUID id) {
        return playerRepository.findById(id).orElse(null);

    }

    @Override
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    @Override
    public void deleteAllPlayers() {
        playerRepository.deleteAll();
    }

    @Override
    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }

    @Override
    public void deletePlayer(UUID id) {
        playerRepository.deleteById(id);
    }


}
