package com.sanguiwara.progression.archetype;

import com.sanguiwara.badges.ModifierType;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.factory.PlayerArchetype;

import java.util.Map;

import static com.sanguiwara.progression.ProgressionSkillGroup.BLOCK;
import static com.sanguiwara.progression.ProgressionSkillGroup.DRIVE;
import static com.sanguiwara.progression.ProgressionSkillGroup.FREE_THROW;
import static com.sanguiwara.progression.ProgressionSkillGroup.MENTAL;
import static com.sanguiwara.progression.ProgressionSkillGroup.PLAYMAKING;
import static com.sanguiwara.progression.ProgressionSkillGroup.RIM_PROTECTION;
import static com.sanguiwara.progression.ProgressionSkillGroup.THREE_POINT;
import static com.sanguiwara.progression.ProgressionSkillGroup.TWO_POINT;

final class WhiteShooterArchetypeDefinition extends PlayerArchetypeDefinition {

    WhiteShooterArchetypeDefinition() {
        super(
                PlayerArchetype.WHITE_SHOOTER,
                Map.of(
                        TrainingType.SHOOTING, 1.20,
                        TrainingType.PLAYMAKING, 1.05,
                        TrainingType.TACTICAL, 1.05,
                        TrainingType.PHYSICAL, 0.90
                ),
                Map.of(
                        THREE_POINT, 1.30,
                        FREE_THROW, 1.20,
                        TWO_POINT, 1.05,
                        PLAYMAKING, 1.05,
                        MENTAL, 1.05,
                        DRIVE, 0.90,
                        RIM_PROTECTION, 0.85,
                        BLOCK, 0.85
                ),
                Map.of(
                        THREE_POINT, 1.30,
                        TWO_POINT, 1.05,
                        PLAYMAKING, 1.05,
                        DRIVE, 0.90,
                        BLOCK, 0.85
                ),
                Map.of(
                        ModifierType.THREE_POINT, 1.35,
                        ModifierType.TWO_POINT, 1.05,
                        ModifierType.ASSIST, 1.05,
                        ModifierType.DRIVE, 0.90,
                        ModifierType.REBOUND, 0.90,
                        ModifierType.BLOCK, 0.85
                )
        );
    }

    @Override
    public void applyInitialStats(Player.PlayerBuilder builder, StatRoller roller) {
        fillAll(builder, roller, 1, 84);

        builder.tir3Pts(roller.roll(78, 99))
                .tir2Pts(roller.roll(72, 94))
                .lancerFranc(roller.roll(78, 99))
                .floater(roller.roll(70, 92))
                .finitionAuCercle(roller.roll(70, 92))
                .basketballIqOff(roller.roll(70, 94))
                .iq(roller.roll(70, 94))
                .passingSkills(roller.roll(65, 90))
                .ballhandling(roller.roll(55, 85))
                .coachability(roller.roll(75, 97))
                .ego(roller.roll(15, 55));

        builder.physique(roller.roll(1, 75))
                .protectionCercle(roller.roll(1, 70))
                .timingBlock(roller.roll(1, 70));
    }
}
