package com.sanguiwara.modifiers;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerModifierTest {

    @Test
    void afterGame_removesNextGameModifier() {
        PlayerModifier modifier = PlayerModifier.nextGameThreePointShotPctBonus(0.05);

        assertThat(modifier.afterGame()).isEmpty();
    }
}
