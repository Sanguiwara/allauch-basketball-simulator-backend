package com.sanguiwara.progression.archetype;

import com.sanguiwara.badges.ModifierType;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.factory.PlayerArchetype;

import java.util.Map;

import static com.sanguiwara.progression.ProgressionSkillGroup.DRIVE;
import static com.sanguiwara.progression.ProgressionSkillGroup.FLOATER;
import static com.sanguiwara.progression.ProgressionSkillGroup.FREE_THROW;
import static com.sanguiwara.progression.ProgressionSkillGroup.INTERIOR_DEFENSE;
import static com.sanguiwara.progression.ProgressionSkillGroup.PERIMETER_DEFENSE;
import static com.sanguiwara.progression.ProgressionSkillGroup.PLAYMAKING;
import static com.sanguiwara.progression.ProgressionSkillGroup.THREE_POINT;
import static com.sanguiwara.progression.ProgressionSkillGroup.TWO_POINT;

final class CroqueurArchetypeDefinition extends PlayerArchetypeDefinition {

    CroqueurArchetypeDefinition() {
        super(
                PlayerArchetype.CROQUEUR,
                Map.of(
                        TrainingType.SHOOTING, 1.15,
                        TrainingType.DEFENSE, 0.80,
                        TrainingType.TACTICAL, 0.90,
                        TrainingType.PLAYMAKING, 0.90
                ),
                Map.of(
                        THREE_POINT, 1.15,
                        TWO_POINT, 1.20,
                        DRIVE, 1.15,
                        FLOATER, 1.10,
                        FREE_THROW, 1.10,
                        PLAYMAKING, 0.85,
                        PERIMETER_DEFENSE, 0.85,
                        INTERIOR_DEFENSE, 0.85
                ),
                Map.of(
                        THREE_POINT, 1.20,
                        TWO_POINT, 1.25,
                        DRIVE, 1.20,
                        PLAYMAKING, 0.85,
                        PERIMETER_DEFENSE, 0.85,
                        INTERIOR_DEFENSE, 0.85
                ),
                Map.of(
                        ModifierType.THREE_POINT, 1.20,
                        ModifierType.TWO_POINT, 1.30,
                        ModifierType.DRIVE, 1.20,
                        ModifierType.ASSIST, 0.85,
                        ModifierType.REBOUND, 0.90,
                        ModifierType.STEAL, 0.85,
                        ModifierType.BLOCK, 0.85,
                        ModifierType.DEF_EXTER, 0.85
                )
        );
    }

    @Override
    public void applyInitialStats(Player.PlayerBuilder builder, StatRoller roller) {
        fillAll(builder, roller, 1, 80);

        builder.tir3Pts(roller.roll(75, 97))
                .tir2Pts(roller.roll(78, 99))
                .lancerFranc(roller.roll(75, 97))
                .floater(roller.roll(70, 95))
                .finitionAuCercle(roller.roll(72, 97))
                .agressivite(roller.roll(85, 99))
                .ballhandling(roller.roll(62, 92))
                .ego(99);

        builder.passingSkills(roller.roll(1, 65))
                .basketballIqDef(roller.roll(1, 72))
                .defExterieur(roller.roll(1, 70))
                .defPoste(roller.roll(1, 70))
                .protectionCercle(roller.roll(1, 65))
                .timingBlock(roller.roll(1, 65))
                .coachability(roller.roll(1, 75));
    }
}
