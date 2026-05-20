package com.sanguiwara.entity;

import com.sanguiwara.badges.ModifierOp;
import com.sanguiwara.badges.Target;
import com.sanguiwara.badges.ModifierType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class PlayerTemporaryModifierEmbeddable {

    @Enumerated(EnumType.STRING)
    @Column(name = "effect_type", nullable = false)
    private ModifierType effectType;

    @Enumerated(EnumType.STRING)
    @Column(name = "target", nullable = false)
    private Target target;

    @Enumerated(EnumType.STRING)
    @Column(name = "op", nullable = false)
    private ModifierOp op;

    @Column(name = "value", nullable = false)
    private double value;

    @Column(name = "games_remaining", nullable = false)
    private int gamesRemaining;
}
