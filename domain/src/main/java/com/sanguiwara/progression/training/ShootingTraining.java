package com.sanguiwara.progression.training;

import com.sanguiwara.badges.ModifierType;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.progression.ProgressionSkillGroup;

import java.util.List;
import java.util.Set;

import static com.sanguiwara.progression.training.TrainableSkill.skill;
import static com.sanguiwara.progression.training.TrainablePlayerStat.FINITION_AU_CERCLE;
import static com.sanguiwara.progression.training.TrainablePlayerStat.FLOATER;
import static com.sanguiwara.progression.training.TrainablePlayerStat.LANCER_FRANC;
import static com.sanguiwara.progression.training.TrainablePlayerStat.TIR_2_PTS;
import static com.sanguiwara.progression.training.TrainablePlayerStat.TIR_3_PTS;

final class ShootingTraining extends SkillTrainingProgression {

    private static final int MIN_DELTA = 1;
    private static final int MAX_DELTA = 2;

    ShootingTraining() {
        super(
                TrainingType.SHOOTING,
                List.of(
                        skill(TIR_3_PTS, ProgressionSkillGroup.THREE_POINT, Player::getTir3Pts, Player::setTir3Pts),
                        skill(TIR_2_PTS, ProgressionSkillGroup.TWO_POINT, Player::getTir2Pts, Player::setTir2Pts),
                        skill(LANCER_FRANC, ProgressionSkillGroup.FREE_THROW, Player::getLancerFranc, Player::setLancerFranc),
                        skill(FLOATER, ProgressionSkillGroup.FLOATER, Player::getFloater, Player::setFloater),
                        skill(FINITION_AU_CERCLE, ProgressionSkillGroup.DRIVE, Player::getFinitionAuCercle, Player::setFinitionAuCercle)
                ),
                MIN_DELTA,
                MAX_DELTA,
                Set.of(ModifierType.THREE_POINT, ModifierType.TWO_POINT, ModifierType.DRIVE)
        );
    }
}
