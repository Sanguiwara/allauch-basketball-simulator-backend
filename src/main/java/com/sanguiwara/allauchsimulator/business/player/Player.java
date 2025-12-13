package com.sanguiwara.allauchsimulator.business.player;

import lombok.Getter;
import lombok.Setter;

// Legacy placeholder kept for backward compatibility during refactor.
// Not a JPA entity anymore. Use domain.player.Player instead.
public class Player {
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private String name;
}
