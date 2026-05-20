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

final class YoungStarArchetypeDefinition extends PlayerArchetypeDefinition {

    YoungStarArchetypeDefinition() {
        super(
                PlayerArchetype.YOUNG_STAR,
                Map.of(
                        TrainingType.SHOOTING, 1.15,
                        TrainingType.DEFENSE, 1.15,
                        TrainingType.PHYSICAL, 1.15,
                        TrainingType.PLAYMAKING, 1.15,
                        TrainingType.TACTICAL, 1.15
                ),
                Map.of(),
                Map.of(
                        THREE_POINT, 1.15,
                        TWO_POINT, 1.15,
                        DRIVE, 1.15,
                        REBOUND, 1.15,
                        STEAL, 1.15,
                        BLOCK, 1.15,
                        RIM_PROTECTION, 1.15
                ),
                Map.of(
                        ModifierType.THREE_POINT, 1.15,
                        ModifierType.TWO_POINT, 1.15,
                        ModifierType.DRIVE, 1.15,
                        ModifierType.REBOUND, 1.15,
                        ModifierType.STEAL, 1.15,
                        ModifierType.ASSIST, 1.15,
                        ModifierType.BLOCK, 1.15,
                        ModifierType.DEF_EXTER, 1.15
                )
        );
    }

    @Override
    public void applyInitialStats(Player.PlayerBuilder builder, StatRoller roller) {
        fillAll(builder, roller, 20, 70);
        builder.potentielSkill(99);
    }
}
