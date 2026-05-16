package com.sanguiwara.service;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.progression.PlayerSeasonState;
import com.sanguiwara.repository.PlayerRepository;
import com.sanguiwara.repository.PlayerSeasonSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerSeasonSnapshotRepository playerSeasonSnapshotRepository;


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

    @Override
    public PlayerSeasonState getPlayerSeasonState(UUID playerId) {
        Player current = playerRepository.findById(playerId).orElse(null);
        if (current == null) {
            return null;
        }

        var snapshots = playerSeasonSnapshotRepository.findByPlayerId(playerId);
        if (snapshots.isEmpty()) {
            return null;
        }

        // TODO Reintroduire la subtilite du leagueSeasonId quand on aura plusieurs saisons.
        var snapshot = snapshots.getFirst();
        return PlayerSeasonState.between(snapshot.leagueSeasonId(), snapshot.player(), current);
    }

}
