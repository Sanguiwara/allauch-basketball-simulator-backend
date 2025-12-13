package com.sanguiwara.allauchsimulator.domain.player;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "player")
@RequiredArgsConstructor
public class Player {
    @Getter
    @Setter
    @Id
    private Long id;
    @Getter
    @Setter
    private String name;
}
