package com.sanguiwara.defense;

import com.sanguiwara.baserecords.DefenseType;

import java.util.EnumMap;
import java.util.List;

public final class DefenseSchemeResolver {

    private final EnumMap<DefenseType, DefensiveScheme> schemes = new EnumMap<>(DefenseType.class);
    private final DefensiveScheme fallbackScheme;

    public DefenseSchemeResolver(List<DefensiveScheme> schemes) {
        for (DefensiveScheme scheme : schemes) {
            this.schemes.put(scheme.type(), scheme);
        }
        this.fallbackScheme = new RegularMan2ManScheme();
    }


    public DefensiveScheme resolve(DefenseType defenseType) {

        return schemes.getOrDefault(defenseType, fallbackScheme);
    }


}

