package com.sanguiwara.service;

import com.sanguiwara.baserecords.Game;
import com.sanguiwara.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class GameServiceImpl implements GameService{
    private final GameRepository gameRepository;

    @Override
    public Game getGameById(UUID gameId) {
        return gameRepository.findById(gameId).orElseThrow();
    }

    @Override
    public List<Game>getAllGamesForATeam(UUID teamId){
        return gameRepository.findAllGamesForAteam(teamId);
    }

    @Override
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }
}
