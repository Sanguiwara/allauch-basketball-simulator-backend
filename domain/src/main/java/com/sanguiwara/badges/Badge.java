package com.sanguiwara.badges;

import java.util.List;
import java.util.Set;

public interface Badge {

    long id();

    String name();

    /**
     * Probability in [0..1] for a player to obtain this badge when a drop roll is attempted.
     */
    double dropRate();

    Set<BadgeType> types();

    List<Modifier> modifiersFor(BadgeType type, Context context);
}

