package com.sanguiwara.service;

import com.sanguiwara.baserecords.Game;
import com.sanguiwara.baserecords.GameSummary;
import com.sanguiwara.progression.ProgressionEventType;
import com.sanguiwara.repository.GameRepository;
import com.sanguiwara.repository.PlayerProgressionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;
    private final PlayerProgressionRepository playerProgressionRepository;

    @Override
    public Game getGameById(UUID gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow();
        game.setPlayerProgressions(playerProgressionRepository.findByEvent(ProgressionEventType.GAME, gameId));
        return game;
    }


    @Override
    public List<Game> getAllGamesForATeam(UUID teamId) {
        return gameRepository.findAllGamesForAteam(teamId);
    }

    @Override
    public List<GameSummary> getAllGameSummaries() {
        return gameRepository.findAllSummaries();
    }
}
