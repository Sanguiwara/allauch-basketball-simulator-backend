package com.sanguiwara.allauchsimulator.infrastructure.persistence.player;

import com.sanguiwara.allauchsimulator.domain.player.Player;
import com.sanguiwara.allauchsimulator.domain.player.PlayerRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaPlayerRepositoryAdapter implements PlayerRepositoryPort {

    private final SpringDataPlayerRepository springDataPlayerRepository;

    @Override
    public Optional<Player> findById(Long id) {
        return springDataPlayerRepository.findById(id);
    }
}
