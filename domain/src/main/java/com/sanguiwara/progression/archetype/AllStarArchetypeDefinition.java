package com.sanguiwara.progression.archetype;

import com.sanguiwara.badges.ModifierType;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.factory.PlayerArchetype;

import java.util.Map;

import static com.sanguiwara.progression.ProgressionSkillGroup.BLOCK;
import static com.sanguiwara.progression.ProgressionSkillGroup.DRIVE;
import static com.sanguiwara.progression.ProgressionSkillGroup.REBOUND;
import static com.sanguiwara.progression.ProgressionSkillGroup.RIM_PROTECTION;
import static com.sanguiwara.progression.ProgressionSkillGroup.STEAL;
import static com.sanguiwara.progression.ProgressionSkillGroup.THREE_POINT;
import static com.sanguiwara.progression.ProgressionSkillGroup.TWO_POINT;

final class AllStarArchetypeDefinition extends PlayerArchetypeDefinition {

    AllStarArchetypeDefinition() {
        super(
                PlayerArchetype.ALL_STAR,
                Map.of(
                        TrainingType.SHOOTING, 0.95,
                        TrainingType.DEFENSE, 0.95,
                        TrainingType.PHYSICAL, 0.95,
                        TrainingType.PLAYMAKING, 0.95,
                        TrainingType.TACTICAL, 0.95
                ),
                Map.of(),
                Map.of(
                        THREE_POINT, 0.95,
                        TWO_POINT, 0.95,
                        DRIVE, 0.95,
                        REBOUND, 0.95,
                        STEAL, 0.95,
                        BLOCK, 0.95,
                        RIM_PROTECTION, 0.95
                ),
                Map.of(
                        ModifierType.THREE_POINT, 0.95,
                        ModifierType.TWO_POINT, 0.95,
                        ModifierType.DRIVE, 0.95,
                        ModifierType.REBOUND, 0.95,
                        ModifierType.STEAL, 0.95,
                        ModifierType.ASSIST, 0.95,
                        ModifierType.BLOCK, 0.95,
                        ModifierType.DEF_EXTER, 0.95
                )
        );
    }

    @Override
    public void applyInitialStats(Player.PlayerBuilder builder, StatRoller roller) {
        fillAll(builder, roller, 75, 99);
        builder.ego(99)
                .coachability(roller.roll(1, 80));
    }
}
