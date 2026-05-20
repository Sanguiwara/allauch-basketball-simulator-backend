package com.sanguiwara.progression.archetype;

import com.sanguiwara.badges.ModifierType;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.factory.PlayerArchetype;

import java.util.Map;

import static com.sanguiwara.progression.ProgressionSkillGroup.DRIVE;
import static com.sanguiwara.progression.ProgressionSkillGroup.FLOATER;
import static com.sanguiwara.progression.ProgressionSkillGroup.FREE_THROW;
import static com.sanguiwara.progression.ProgressionSkillGroup.THREE_POINT;
import static com.sanguiwara.progression.ProgressionSkillGroup.TWO_POINT;

final class ThreePointShooterArchetypeDefinition extends PlayerArchetypeDefinition {

    ThreePointShooterArchetypeDefinition() {
        super(
                PlayerArchetype.THREE_POINT_SHOOTER,
                Map.of(
                        TrainingType.SHOOTING, 1.25,
                        TrainingType.PHYSICAL, 1.05,
                        TrainingType.DEFENSE, 0.90
                ),
                Map.of(
                        THREE_POINT, 1.45,
                        FREE_THROW, 1.10,
                        TWO_POINT, 0.90,
                        DRIVE, 0.75,
                        FLOATER, 0.80
                ),
                Map.of(
                        THREE_POINT, 1.40,
                        TWO_POINT, 0.85,
                        DRIVE, 0.80
                ),
                Map.of(
                        ModifierType.THREE_POINT, 1.50,
                        ModifierType.TWO_POINT, 0.85,
                        ModifierType.DRIVE, 0.80
                )
        );
    }

    @Override
    public void applyInitialStats(Player.PlayerBuilder builder, StatRoller roller) {
        fillAll(builder, roller, 1, 82);

        builder.speed(roller.roll(75, 99))
                .size(roller.roll(75, 99))
                .endurance(roller.roll(75, 99))
                .tir3Pts(roller.roll(90, 99))
                .agressivite(roller.roll(70, 99))
                .basketballIqOff(roller.roll(75, 99));
    }
}
