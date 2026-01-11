package com.sanguiwara.repository.pgsql;

import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.mapper.InGamePlayerMapper;
import com.sanguiwara.repository.InGamePlayerRepository;
import com.sanguiwara.repository.jpa.InGamePlayerJpaRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InGamePlayerRepositoryPGSQL implements InGamePlayerRepository {

    private final InGamePlayerJpaRepository inGamePlayerJpaRepository;
    private final InGamePlayerMapper inGamePlayerMapper;

    @Override
    public @NonNull Optional<InGamePlayer> findById(Long id) {
        return inGamePlayerJpaRepository.findById(id).map(inGamePlayerMapper::toDomain);
    }

    @Override
    public List<InGamePlayer> findAll() {
        return inGamePlayerJpaRepository.findAll().stream()
                .map(inGamePlayerMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public InGamePlayer save(InGamePlayer inGamePlayer) {
        var entity = inGamePlayerMapper.toEntity(inGamePlayer);
        var saved = inGamePlayerJpaRepository.save(entity);
        return inGamePlayerMapper.toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        inGamePlayerJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        inGamePlayerJpaRepository.deleteAll();
    }
}
