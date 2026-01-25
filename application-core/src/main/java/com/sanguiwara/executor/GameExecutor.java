package com.sanguiwara.executor;

import com.sanguiwara.baserecords.Game;
import com.sanguiwara.calculator.GameSimulator;
import com.sanguiwara.repository.GameRepository;
import com.sanguiwara.result.GameResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameExecutor {

    private final GameSimulator gameSimulator;
    private final GameRepository gameRepository;


    public void executeGame(UUID gameId) {
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        Game game = optionalGame.orElseThrow();
        GameResult boxScore = gameSimulator.calculateGame(game.getHomeGamePlan(), game.getAwayGamePlan());
        game.setGameResult(boxScore);
        gameRepository.save(game);
    }
}