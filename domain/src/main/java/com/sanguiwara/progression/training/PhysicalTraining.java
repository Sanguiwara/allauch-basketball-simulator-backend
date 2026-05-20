package com.sanguiwara.progression.training;

import com.sanguiwara.badges.ModifierType;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.progression.ProgressionSkillGroup;

import java.util.List;
import java.util.Set;

import static com.sanguiwara.progression.training.TrainableSkill.skill;
import static com.sanguiwara.progression.training.TrainablePlayerStat.ENDURANCE;
import static com.sanguiwara.progression.training.TrainablePlayerStat.PHYSIQUE;
import static com.sanguiwara.progression.training.TrainablePlayerStat.SOLIDITE;
import static com.sanguiwara.progression.training.TrainablePlayerStat.SPEED;

final class PhysicalTraining extends SkillTrainingProgression {

    private static final int MIN_DELTA = 1;
    private static final int MAX_DELTA = 3;

    PhysicalTraining() {
        super(
                TrainingType.PHYSICAL,
                List.of(
                        skill(PHYSIQUE, ProgressionSkillGroup.ATHLETIC, Player::getPhysique, Player::setPhysique),
                        skill(SPEED, ProgressionSkillGroup.ATHLETIC, Player::getSpeed, Player::setSpeed),
                        skill(ENDURANCE, ProgressionSkillGroup.ATHLETIC, Player::getEndurance, Player::setEndurance),
                        skill(SOLIDITE, ProgressionSkillGroup.ATHLETIC, Player::getSolidite, Player::setSolidite)
                ),
                MIN_DELTA,
                MAX_DELTA,
                Set.of(ModifierType.DRIVE, ModifierType.REBOUND, ModifierType.BLOCK)
        );
    }
}
