package com.sanguiwara.progression.archetype;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.factory.PlayerArchetype;

import java.util.Map;

final class AllAroundArchetypeDefinition extends PlayerArchetypeDefinition {

    AllAroundArchetypeDefinition() {
        super(PlayerArchetype.ALL_AROUND, Map.of(), Map.of(), Map.of(), Map.of());
    }

    @Override
    public void applyInitialStats(Player.PlayerBuilder builder, StatRoller roller) {
        fillAll(builder, roller, 60, 90);
        builder.ego(roller.roll(20, 65));
    }
}
