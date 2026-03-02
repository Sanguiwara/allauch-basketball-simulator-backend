package com.sanguiwara.defense;

import com.sanguiwara.baserecords.DefenseType;

import java.util.EnumMap;
import java.util.List;

public final class DefenseSchemeResolver {

    private final EnumMap<DefenseType, DefensiveScheme> schemes = new EnumMap<>(DefenseType.class);

    public DefenseSchemeResolver(List<DefensiveScheme> schemes) {
        for (DefensiveScheme scheme : schemes) {
            this.schemes.put(scheme.type(), scheme);
        }
    }


    public DefensiveScheme resolve(DefenseType defenseType) {

        return schemes.get(defenseType);
    }


}

