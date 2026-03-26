package com.sanguiwara.service;

import com.sanguiwara.baserecords.Player;

import java.util.List;
import java.util.UUID;

public interface PlayerService {


    Player getPlayer(UUID id);

    List<Player> getAllPlayers();

    void deleteAllPlayers();

    Player savePlayer(Player player);

    void deletePlayer(UUID id);
}
