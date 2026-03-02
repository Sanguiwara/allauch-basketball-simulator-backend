package com.sanguiwara.badges;

import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class StandardBadge implements Badge {

    private final long id;
    private final String name;
    private final double dropRate;
    private final Set<BadgeType> types;
    private final EnumMap<BadgeType, List<Modifier>> modifiersByType;

    public StandardBadge(long id, String name, double dropRate, Set<BadgeType> types, EnumMap<BadgeType, List<Modifier>> modifiersByType) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name");
        this.dropRate = dropRate;
        this.types = Objects.requireNonNull(types, "types");
        this.modifiersByType = Objects.requireNonNull(modifiersByType, "modifiersByType");
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public double dropRate() {
        return dropRate;
    }

    @Override
    public Set<BadgeType> types() {
        return types;
    }

    @Override
    public List<Modifier> modifiersFor(BadgeType type, Context context) {
        return modifiersByType.getOrDefault(type, List.of());
    }
}
