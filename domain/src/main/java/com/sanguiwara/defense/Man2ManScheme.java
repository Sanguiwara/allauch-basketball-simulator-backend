package com.sanguiwara.defense;

import com.sanguiwara.modifiers.PlayerModifierEngine;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Man2ManScheme implements DefensiveScheme {

    protected static final int TOTAL_MINUTES_FOR_TEAM = 200;

    protected final PlayerModifierEngine modifierEngine;

}
