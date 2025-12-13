package com.sanguiwara.allauchsimulator.infrastructure.persistence.player;

import com.sanguiwara.allauchsimulator.domain.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPlayerRepository extends JpaRepository<Player, Long> {
}
