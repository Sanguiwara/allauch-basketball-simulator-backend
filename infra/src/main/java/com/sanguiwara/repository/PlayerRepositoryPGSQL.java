package com.sanguiwara.repository;

import baserecords.Player;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import com.sanguiwara.mapper.PlayerMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PlayerRepositoryPGSQL implements PlayerRepository {

    private final PlayerJpaRepository playerJpaRepository;
    private final PlayerMapper playerMapper;


    @Override
    public  @NonNull Optional<Player> findById(Long id) {
        return playerJpaRepository.findById(id).map(playerMapper::toDomain);

    }
}
