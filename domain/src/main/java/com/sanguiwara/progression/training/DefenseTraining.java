package com.sanguiwara.progression.training;

import com.sanguiwara.badges.ModifierType;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.progression.ProgressionSkillGroup;

import java.util.List;
import java.util.Set;

import static com.sanguiwara.progression.training.TrainableSkill.skill;
import static com.sanguiwara.progression.training.TrainablePlayerStat.DEF_EXTERIEUR;
import static com.sanguiwara.progression.training.TrainablePlayerStat.DEF_POSTE;
import static com.sanguiwara.progression.training.TrainablePlayerStat.PROTECTION_CERCLE;
import static com.sanguiwara.progression.training.TrainablePlayerStat.STEAL;
import static com.sanguiwara.progression.training.TrainablePlayerStat.TIMING_BLOCK;

final class DefenseTraining extends SkillTrainingProgression {

    private static final int MIN_DELTA = 1;
    private static final int MAX_DELTA = 2;

    DefenseTraining() {
        super(
                TrainingType.DEFENSE,
                List.of(
                        skill(DEF_EXTERIEUR, ProgressionSkillGroup.PERIMETER_DEFENSE, Player::getDefExterieur, Player::setDefExterieur),
                        skill(DEF_POSTE, ProgressionSkillGroup.INTERIOR_DEFENSE, Player::getDefPoste, Player::setDefPoste),
                        skill(PROTECTION_CERCLE, ProgressionSkillGroup.RIM_PROTECTION, Player::getProtectionCercle, Player::setProtectionCercle),
                        skill(STEAL, ProgressionSkillGroup.STEAL, Player::getSteal, Player::setSteal),
                        skill(TIMING_BLOCK, ProgressionSkillGroup.BLOCK, Player::getTimingBlock, Player::setTimingBlock)
                ),
                MIN_DELTA,
                MAX_DELTA,
                Set.of(ModifierType.STEAL, ModifierType.BLOCK, ModifierType.DEF_EXTER)
        );
    }
}
