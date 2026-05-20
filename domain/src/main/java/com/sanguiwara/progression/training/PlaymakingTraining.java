package com.sanguiwara.progression.training;

import com.sanguiwara.badges.ModifierType;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.progression.ProgressionSkillGroup;

import java.util.List;
import java.util.Set;

import static com.sanguiwara.progression.training.TrainableSkill.skill;
import static com.sanguiwara.progression.training.TrainablePlayerStat.BALLHANDLING;
import static com.sanguiwara.progression.training.TrainablePlayerStat.BASKETBALL_IQ_OFF;
import static com.sanguiwara.progression.training.TrainablePlayerStat.IQ;
import static com.sanguiwara.progression.training.TrainablePlayerStat.PASSING_SKILLS;

final class PlaymakingTraining extends SkillTrainingProgression {

    private static final int MIN_DELTA = 1;
    private static final int MAX_DELTA = 3;

    PlaymakingTraining() {
        super(
                TrainingType.PLAYMAKING,
                List.of(
                        skill(BALLHANDLING, ProgressionSkillGroup.DRIVE, Player::getBallhandling, Player::setBallhandling),
                        skill(PASSING_SKILLS, ProgressionSkillGroup.PLAYMAKING, Player::getPassingSkills, Player::setPassingSkills),
                        skill(BASKETBALL_IQ_OFF, ProgressionSkillGroup.MENTAL, Player::getBasketballIqOff, Player::setBasketballIqOff),
                        skill(IQ, ProgressionSkillGroup.MENTAL, Player::getIq, Player::setIq)
                ),
                MIN_DELTA,
                MAX_DELTA,
                Set.of(ModifierType.ASSIST, ModifierType.DRIVE)
        );
    }
}
