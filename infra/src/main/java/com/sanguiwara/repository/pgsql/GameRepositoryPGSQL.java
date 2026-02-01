package com.sanguiwara.repository.pgsql;

import com.sanguiwara.baserecords.Game;
import com.sanguiwara.mapper.GameMapper;
import com.sanguiwara.repository.jpa.GameJpaRepository;
import com.sanguiwara.repository.GameRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class GameRepositoryPGSQL implements GameRepository {

    private final GameJpaRepository gameJpaRepository;
    private final GameMapper gameMapper;



    @Override
    public  @NonNull Optional<Game> findById(UUID id) {
        return gameJpaRepository.findById(id).map(gameMapper::toDomain);

    }

    @Override
    public List<Game> findAllGamesForAteam(UUID teamId) {
//        return findAll().stream().filter(game-> game.getHomeGamePlan().getOwnerTeam().getId().equals(teamId) ||
//                game.getAwayGamePlan().getOwnerTeam().getId().equals(teamId)).toList();
        return gameJpaRepository.findAllByParticipantTeamId(teamId).stream()
                .map(gameMapper::toDomain)
                .toList();
    }




    @Override
    public List<Game> findAll() {
        return gameJpaRepository.findAll().stream()
                .map(gameMapper::toDomain)
                .toList();
    }

    @Override
    public Game save(Game game) {
        var entity = gameMapper.toEntity(game);
        var saved = gameJpaRepository.save(entity);
        return gameMapper.toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void deleteAll() {

    }
}
