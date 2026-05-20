package com.sanguiwara.progression.archetype;

import com.sanguiwara.badges.ModifierType;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.factory.PlayerArchetype;

import java.util.Map;

import static com.sanguiwara.progression.ProgressionSkillGroup.ATHLETIC;
import static com.sanguiwara.progression.ProgressionSkillGroup.DRIVE;
import static com.sanguiwara.progression.ProgressionSkillGroup.FLOATER;
import static com.sanguiwara.progression.ProgressionSkillGroup.PLAYMAKING;
import static com.sanguiwara.progression.ProgressionSkillGroup.THREE_POINT;
import static com.sanguiwara.progression.ProgressionSkillGroup.TWO_POINT;

final class DriveSpecialistArchetypeDefinition extends PlayerArchetypeDefinition {

    DriveSpecialistArchetypeDefinition() {
        super(
                PlayerArchetype.DRIVE_SPECIALIST,
                Map.of(
                        TrainingType.SHOOTING, 1.15,
                        TrainingType.PHYSICAL, 1.10,
                        TrainingType.PLAYMAKING, 1.10,
                        TrainingType.DEFENSE, 0.90
                ),
                Map.of(
                        DRIVE, 1.45,
                        FLOATER, 1.30,
                        ATHLETIC, 1.10,
                        PLAYMAKING, 1.10,
                        TWO_POINT, 1.05,
                        THREE_POINT, 0.75
                ),
                Map.of(
                        DRIVE, 1.45,
                        TWO_POINT, 1.05,
                        THREE_POINT, 0.75
                ),
                Map.of(
                        ModifierType.DRIVE, 1.50,
                        ModifierType.TWO_POINT, 1.05,
                        ModifierType.THREE_POINT, 0.75,
                        ModifierType.ASSIST, 1.10
                )
        );
    }

    @Override
    public void applyInitialStats(Player.PlayerBuilder builder, StatRoller roller) {
        fillAll(builder, roller, 1, 82);

        builder.speed(roller.roll(80, 99))
                .size(roller.roll(75, 99))
                .endurance(roller.roll(75, 99))
                .ballhandling(roller.roll(80, 99))
                .finitionAuCercle(roller.roll(90, 99))
                .floater(roller.roll(90, 99))
                .agressivite(roller.roll(70, 99))
                .basketballIqOff(roller.roll(75, 99));
    }
}
