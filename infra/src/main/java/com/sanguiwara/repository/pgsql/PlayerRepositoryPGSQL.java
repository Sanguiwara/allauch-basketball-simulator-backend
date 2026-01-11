package com.sanguiwara.repository.pgsql;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.repository.jpa.PlayerJpaRepository;
import com.sanguiwara.repository.PlayerRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import com.sanguiwara.mapper.PlayerMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PlayerRepositoryPGSQL implements PlayerRepository {

    private final PlayerJpaRepository playerJpaRepository;
    private final PlayerMapper playerMapper;


    @Override
    public  @NonNull Optional<Player> findById(UUID id) {
        return playerJpaRepository.findById(id).map(playerMapper::toDomain);

    }

    @Override
    public List<Player> findAll() {
        return playerJpaRepository.findAll().stream()
                .map(playerMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Player save(Player player) {
        var entity = playerMapper.toEntity(player);
        var savedEntity = playerJpaRepository.save(entity);
        return playerMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(UUID id) {
        playerJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        playerJpaRepository.deleteAll();
    }



}
