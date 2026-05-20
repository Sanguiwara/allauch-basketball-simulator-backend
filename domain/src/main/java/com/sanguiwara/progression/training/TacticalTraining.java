package com.sanguiwara.progression.training;

import com.sanguiwara.badges.ModifierType;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.progression.ProgressionSkillGroup;

import java.util.List;
import java.util.Set;

import static com.sanguiwara.progression.training.TrainableSkill.skill;
import static com.sanguiwara.progression.training.TrainablePlayerStat.BASKETBALL_IQ_DEF;
import static com.sanguiwara.progression.training.TrainablePlayerStat.BASKETBALL_IQ_OFF;
import static com.sanguiwara.progression.training.TrainablePlayerStat.IQ;

final class TacticalTraining extends SkillTrainingProgression {

    private static final int MIN_DELTA = 2;
    private static final int MAX_DELTA = 3;

    TacticalTraining() {
        super(
                TrainingType.TACTICAL,
                List.of(
                        skill(BASKETBALL_IQ_OFF, ProgressionSkillGroup.MENTAL, Player::getBasketballIqOff, Player::setBasketballIqOff),
                        skill(BASKETBALL_IQ_DEF, ProgressionSkillGroup.MENTAL, Player::getBasketballIqDef, Player::setBasketballIqDef),
                        skill(IQ, ProgressionSkillGroup.MENTAL, Player::getIq, Player::setIq)
                ),
                MIN_DELTA,
                MAX_DELTA,
                Set.of(ModifierType.ASSIST, ModifierType.DEF_EXTER)
        );
    }
}
