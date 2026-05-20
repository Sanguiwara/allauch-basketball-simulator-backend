package com.sanguiwara.progression.training;

import com.sanguiwara.badges.ModifierType;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.progression.ProgressionSkillGroup;
import com.sanguiwara.progression.archetype.PlayerArchetypeDefinition;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static com.sanguiwara.progression.training.TrainableSkill.skill;
import static com.sanguiwara.progression.training.TrainablePlayerStat.BALLHANDLING;
import static com.sanguiwara.progression.training.TrainablePlayerStat.BASKETBALL_IQ_DEF;
import static com.sanguiwara.progression.training.TrainablePlayerStat.BASKETBALL_IQ_OFF;
import static com.sanguiwara.progression.training.TrainablePlayerStat.DEF_EXTERIEUR;
import static com.sanguiwara.progression.training.TrainablePlayerStat.DEF_POSTE;
import static com.sanguiwara.progression.training.TrainablePlayerStat.FINITION_AU_CERCLE;
import static com.sanguiwara.progression.training.TrainablePlayerStat.PASSING_SKILLS;
import static com.sanguiwara.progression.training.TrainablePlayerStat.PROTECTION_CERCLE;
import static com.sanguiwara.progression.training.TrainablePlayerStat.STEAL;
import static com.sanguiwara.progression.training.TrainablePlayerStat.TIR_2_PTS;
import static com.sanguiwara.progression.training.TrainablePlayerStat.TIR_3_PTS;

final class FreePlayTraining implements TrainingProgression {

    private static final int SKILL_MIN_DELTA = 1;
    private static final int SKILL_MAX_DELTA = 1;
    private static final double SKILL_PROGRESSION_MULTIPLIER = 0.35;

    private static final int MORALE_MIN_DELTA = 1;
    private static final int MORALE_MAX_DELTA = 1;
    private static final double BADGE_DROP_MULTIPLIER = 0.30;

    private static final List<TrainableSkill> SKILLS = List.of(
            skill(TIR_3_PTS, ProgressionSkillGroup.THREE_POINT, Player::getTir3Pts, Player::setTir3Pts),
            skill(TIR_2_PTS, ProgressionSkillGroup.TWO_POINT, Player::getTir2Pts, Player::setTir2Pts),
            skill(FINITION_AU_CERCLE, ProgressionSkillGroup.DRIVE, Player::getFinitionAuCercle, Player::setFinitionAuCercle),
            skill(BALLHANDLING, ProgressionSkillGroup.DRIVE, Player::getBallhandling, Player::setBallhandling),
            skill(PASSING_SKILLS, ProgressionSkillGroup.PLAYMAKING, Player::getPassingSkills, Player::setPassingSkills),
            skill(BASKETBALL_IQ_OFF, ProgressionSkillGroup.MENTAL, Player::getBasketballIqOff, Player::setBasketballIqOff),
            skill(DEF_EXTERIEUR, ProgressionSkillGroup.PERIMETER_DEFENSE, Player::getDefExterieur, Player::setDefExterieur),
            skill(DEF_POSTE, ProgressionSkillGroup.INTERIOR_DEFENSE, Player::getDefPoste, Player::setDefPoste),
            skill(PROTECTION_CERCLE, ProgressionSkillGroup.RIM_PROTECTION, Player::getProtectionCercle, Player::setProtectionCercle),
            skill(STEAL, ProgressionSkillGroup.STEAL, Player::getSteal, Player::setSteal),
            skill(BASKETBALL_IQ_DEF, ProgressionSkillGroup.MENTAL, Player::getBasketballIqDef, Player::setBasketballIqDef)
    );

    private static final Set<ModifierType> ELIGIBLE_BADGE_TYPES = Set.of(
            ModifierType.THREE_POINT,
            ModifierType.TWO_POINT,
            ModifierType.DRIVE,
            ModifierType.ASSIST,
            ModifierType.STEAL,
            ModifierType.BLOCK,
            ModifierType.DEF_EXTER
    );

    @Override
    public TrainingType type() {
        return TrainingType.FREE_PLAY;
    }

    @Override
    public List<ModifierType> badgeModifierTypes() {
        return ELIGIBLE_BADGE_TYPES.stream()
                .sorted(Comparator.comparing(ModifierType::name))
                .toList();
    }

    @Override
    public double badgeDropRateMultiplier() {
        return BADGE_DROP_MULTIPLIER;
    }

    @Override
    public void apply(Player player, PlayerArchetypeDefinition archetype, TrainingEngine trainingEngine) {
        trainingEngine.applySkillRolls(type(), archetype, player, SKILLS, SKILL_MIN_DELTA, SKILL_MAX_DELTA, SKILL_PROGRESSION_MULTIPLIER);
        trainingEngine.applyMoraleRoll(type(), archetype, player, MORALE_MIN_DELTA, MORALE_MAX_DELTA);
        trainingEngine.applyBadgeUnlock(archetype, player, ELIGIBLE_BADGE_TYPES, BADGE_DROP_MULTIPLIER);
    }
}
