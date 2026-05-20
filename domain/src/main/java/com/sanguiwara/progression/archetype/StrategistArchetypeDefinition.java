package com.sanguiwara.progression.archetype;

import com.sanguiwara.badges.ModifierType;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.factory.PlayerArchetype;

import java.util.Map;

import static com.sanguiwara.progression.ProgressionSkillGroup.DRIVE;
import static com.sanguiwara.progression.ProgressionSkillGroup.MENTAL;
import static com.sanguiwara.progression.ProgressionSkillGroup.PLAYMAKING;
import static com.sanguiwara.progression.ProgressionSkillGroup.THREE_POINT;
import static com.sanguiwara.progression.ProgressionSkillGroup.TWO_POINT;

final class StrategistArchetypeDefinition extends PlayerArchetypeDefinition {

    StrategistArchetypeDefinition() {
        super(
                PlayerArchetype.STRATEGIST,
                Map.of(
                        TrainingType.PLAYMAKING, 1.25,
                        TrainingType.TACTICAL, 1.25,
                        TrainingType.SHOOTING, 1.05,
                        TrainingType.DEFENSE, 1.05
                ),
                Map.of(
                        PLAYMAKING, 1.25,
                        MENTAL, 1.20,
                        THREE_POINT, 1.05,
                        TWO_POINT, 1.05,
                        DRIVE, 1.05
                ),
                Map.of(
                        PLAYMAKING, 1.25,
                        MENTAL, 1.20,
                        THREE_POINT, 1.05,
                        TWO_POINT, 1.05,
                        DRIVE, 1.05
                ),
                Map.of(
                        ModifierType.ASSIST, 1.40,
                        ModifierType.THREE_POINT, 1.05,
                        ModifierType.TWO_POINT, 1.05,
                        ModifierType.DRIVE, 1.05,
                        ModifierType.STEAL, 1.05,
                        ModifierType.DEF_EXTER, 1.05
                )
        );
    }

    @Override
    public void applyInitialStats(Player.PlayerBuilder builder, StatRoller roller) {
        fillAll(builder, roller, 1, 82);

        builder.speed(roller.roll(75, 99))
                .size(roller.roll(75, 99))
                .endurance(roller.roll(75, 99))
                .passingSkills(roller.roll(75, 99))
                .basketballIqOff(roller.roll(75, 99))
                .ballhandling(roller.roll(75, 99))
                .tir3Pts(roller.roll(75, 99))
                .tir2Pts(roller.roll(75, 99))
                .finitionAuCercle(roller.roll(75, 99))
                .floater(roller.roll(75, 99));
    }
}
