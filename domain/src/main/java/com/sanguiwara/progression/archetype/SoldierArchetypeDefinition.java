package com.sanguiwara.progression.archetype;

import com.sanguiwara.badges.ModifierType;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.factory.PlayerArchetype;

import java.util.Map;

import static com.sanguiwara.progression.ProgressionSkillGroup.ATHLETIC;
import static com.sanguiwara.progression.ProgressionSkillGroup.BLOCK;
import static com.sanguiwara.progression.ProgressionSkillGroup.DRIVE;
import static com.sanguiwara.progression.ProgressionSkillGroup.INTERIOR_DEFENSE;
import static com.sanguiwara.progression.ProgressionSkillGroup.PERIMETER_DEFENSE;
import static com.sanguiwara.progression.ProgressionSkillGroup.REBOUND;
import static com.sanguiwara.progression.ProgressionSkillGroup.RIM_PROTECTION;
import static com.sanguiwara.progression.ProgressionSkillGroup.STEAL;
import static com.sanguiwara.progression.ProgressionSkillGroup.THREE_POINT;
import static com.sanguiwara.progression.ProgressionSkillGroup.TWO_POINT;

final class SoldierArchetypeDefinition extends PlayerArchetypeDefinition {

    SoldierArchetypeDefinition() {
        super(
                PlayerArchetype.SOLDIER,
                Map.of(
                        TrainingType.DEFENSE, 1.25,
                        TrainingType.PHYSICAL, 1.20,
                        TrainingType.SHOOTING, 0.85,
                        TrainingType.PLAYMAKING, 0.95
                ),
                Map.ofEntries(
                        Map.entry(PERIMETER_DEFENSE, 1.15),
                        Map.entry(INTERIOR_DEFENSE, 1.15),
                        Map.entry(RIM_PROTECTION, 1.20),
                        Map.entry(REBOUND, 1.25),
                        Map.entry(STEAL, 1.20),
                        Map.entry(BLOCK, 1.25),
                        Map.entry(ATHLETIC, 1.15),
                        Map.entry(THREE_POINT, 0.80),
                        Map.entry(TWO_POINT, 0.90),
                        Map.entry(DRIVE, 0.95)
                ),
                Map.ofEntries(
                        Map.entry(REBOUND, 1.25),
                        Map.entry(STEAL, 1.25),
                        Map.entry(BLOCK, 1.25),
                        Map.entry(RIM_PROTECTION, 1.20),
                        Map.entry(PERIMETER_DEFENSE, 1.15),
                        Map.entry(INTERIOR_DEFENSE, 1.15),
                        Map.entry(THREE_POINT, 0.80),
                        Map.entry(TWO_POINT, 0.90),
                        Map.entry(DRIVE, 0.95)
                ),
                Map.ofEntries(
                        Map.entry(ModifierType.REBOUND, 1.35),
                        Map.entry(ModifierType.STEAL, 1.30),
                        Map.entry(ModifierType.BLOCK, 1.30),
                        Map.entry(ModifierType.DEF_EXTER, 1.15),
                        Map.entry(ModifierType.THREE_POINT, 0.80),
                        Map.entry(ModifierType.TWO_POINT, 0.90),
                        Map.entry(ModifierType.DRIVE, 0.95)
                )
        );
    }

    @Override
    public void applyInitialStats(Player.PlayerBuilder builder, StatRoller roller) {
        fillAll(builder, roller, 1, 78);

        builder.physique(roller.roll(90, 99))
                .solidite(roller.roll(80, 99))
                .endurance(roller.roll(78, 99))
                .defExterieur(roller.roll(85, 99))
                .defPoste(roller.roll(85, 99))
                .protectionCercle(roller.roll(85, 99))
                .timingRebond(roller.roll(88, 99))
                .agressiviteRebond(roller.roll(90, 99))
                .steal(roller.roll(90, 99))
                .timingBlock(roller.roll(90, 99))
                .size(roller.roll(88, 99))
                .weight(roller.roll(88, 99))
                .coachability(roller.roll(90, 99))
                .speed(roller.roll(90, 99))
                .iq(roller.roll(85, 99))
                .basketballIqDef(roller.roll(85, 99))
                .ego(roller.roll(20, 60));

        builder.tir3Pts(roller.roll(25, 65))
                .ballhandling(roller.roll(35, 70))
                .passingSkills(roller.roll(35, 72))
                .tir2Pts(roller.roll(35, 70));
    }
}
