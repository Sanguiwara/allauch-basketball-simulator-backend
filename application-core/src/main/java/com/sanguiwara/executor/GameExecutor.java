package com.sanguiwara.executor;

import com.sanguiwara.result.BoxScore;
import com.sanguiwara.baserecords.Game;
import com.sanguiwara.repository.GameRepository;
import com.sanguiwara.calculator.GameCalculator;
import lombok.RequiredArgsConstructor;import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameExecutor  {

    private final GameCalculator gameCalculator;
    private final GameRepository gameRepository;




    public void executeGame(UUID gameId) {
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        Game game  = optionalGame.orElseThrow();
        BoxScore boxScore = gameCalculator.calculate(game.getHomeGamePlan(), game.getAwayGamePlan());
        game.setBoxScore(boxScore);
        gameRepository.save(game);
    }
}