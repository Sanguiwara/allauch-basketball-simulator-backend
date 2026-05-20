package com.sanguiwara.progression.archetype;

import com.sanguiwara.factory.PlayerArchetype;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class PlayerArchetypes {

    private static final Map<PlayerArchetype, PlayerArchetypeDefinition> DEFINITIONS = definitionsByType();

    private PlayerArchetypes() {
    }

    public static PlayerArchetypeDefinition definitionFor(PlayerArchetype type) {
        Objects.requireNonNull(type, "type");
        PlayerArchetypeDefinition definition = DEFINITIONS.get(type);
        if (definition == null) {
            throw new IllegalArgumentException("Unsupported player archetype: " + type);
        }
        return definition;
    }

    private static Map<PlayerArchetype, PlayerArchetypeDefinition> definitionsByType() {
        List<PlayerArchetypeDefinition> definitions = List.of(
                new SoldierArchetypeDefinition(),
                new StrategistArchetypeDefinition(),
                new CroqueurArchetypeDefinition(),
                new WhiteShooterArchetypeDefinition(),
                new ThreePointShooterArchetypeDefinition(),
                new TwoPointScorerArchetypeDefinition(),
                new DriveSpecialistArchetypeDefinition(),
                new YoungStarArchetypeDefinition(),
                new AllAroundArchetypeDefinition(),
                new AllStarArchetypeDefinition()
        );

        EnumMap<PlayerArchetype, PlayerArchetypeDefinition> byType = new EnumMap<>(PlayerArchetype.class);
        for (PlayerArchetypeDefinition definition : definitions) {
            PlayerArchetypeDefinition previous = byType.put(definition.type(), definition);
            if (previous != null) {
                throw new IllegalStateException("Duplicate archetype definition for " + definition.type());
            }
        }

        if (byType.size() != PlayerArchetype.values().length) {
            throw new IllegalStateException("Every player archetype must have a definition");
        }

        return Map.copyOf(byType);
    }
}
