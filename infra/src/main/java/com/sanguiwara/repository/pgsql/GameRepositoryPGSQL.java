package com.sanguiwara.repository.pgsql;

import com.sanguiwara.baserecords.Game;
import com.sanguiwara.mapper.GameMapper;
import com.sanguiwara.repository.jpa.GameJpaRepository;
import com.sanguiwara.repository.GameRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
    public Game save(Game game) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void deleteAll() {

    }
}
