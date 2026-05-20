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

final class TwoPointScorerArchetypeDefinition extends PlayerArchetypeDefinition {

    TwoPointScorerArchetypeDefinition() {
        super(
                PlayerArchetype.TWO_POINT_SCORER,
                Map.of(
                        TrainingType.SHOOTING, 1.25,
                        TrainingType.PHYSICAL, 1.05,
                        TrainingType.DEFENSE, 0.90
                ),
                Map.of(
                        TWO_POINT, 1.45,
                        DRIVE, 1.10,
                        FLOATER, 1.05,
                        THREE_POINT, 0.75,
                        FREE_THROW, 1.05
                ),
                Map.of(
                        TWO_POINT, 1.40,
                        DRIVE, 1.05,
                        THREE_POINT, 0.80
                ),
                Map.of(
                        ModifierType.TWO_POINT, 1.50,
                        ModifierType.DRIVE, 1.05,
                        ModifierType.THREE_POINT, 0.80
                )
        );
    }

    @Override
    public void applyInitialStats(Player.PlayerBuilder builder, StatRoller roller) {
        fillAll(builder, roller, 1, 82);

        builder.speed(roller.roll(75, 99))
                .size(roller.roll(75, 99))
                .endurance(roller.roll(75, 99))
                .finitionAuCercle(roller.roll(65, 90))
                .tir2Pts(roller.roll(90, 99))
                .agressivite(roller.roll(70, 99))
                .floater(roller.roll(40, 75))
                .basketballIqOff(roller.roll(75, 90));
    }
}
