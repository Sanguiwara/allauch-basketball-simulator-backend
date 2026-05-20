package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.dto.TrainingProgressionDTO;
import com.sanguiwara.dto.TrainingProgressionImpactDTO;
import com.sanguiwara.progression.ProgressionSkillGroup;
import com.sanguiwara.progression.training.TrainablePlayerStat;
import com.sanguiwara.progression.training.TrainingProgression;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static com.sanguiwara.progression.ProgressionSkillGroup.ATHLETIC;
import static com.sanguiwara.progression.ProgressionSkillGroup.BLOCK;
import static com.sanguiwara.progression.ProgressionSkillGroup.DRIVE;
import static com.sanguiwara.progression.ProgressionSkillGroup.FREE_THROW;
import static com.sanguiwara.progression.ProgressionSkillGroup.INTERIOR_DEFENSE;
import static com.sanguiwara.progression.ProgressionSkillGroup.MENTAL;
import static com.sanguiwara.progression.ProgressionSkillGroup.PERIMETER_DEFENSE;
import static com.sanguiwara.progression.ProgressionSkillGroup.PLAYMAKING;
import static com.sanguiwara.progression.ProgressionSkillGroup.RIM_PROTECTION;
import static com.sanguiwara.progression.ProgressionSkillGroup.THREE_POINT;
import static com.sanguiwara.progression.ProgressionSkillGroup.TWO_POINT;
import static com.sanguiwara.progression.training.TrainablePlayerStat.BALLHANDLING;
import static com.sanguiwara.progression.training.TrainablePlayerStat.BASKETBALL_IQ_DEF;
import static com.sanguiwara.progression.training.TrainablePlayerStat.BASKETBALL_IQ_OFF;
import static com.sanguiwara.progression.training.TrainablePlayerStat.DEF_EXTERIEUR;
import static com.sanguiwara.progression.training.TrainablePlayerStat.DEF_POSTE;
import static com.sanguiwara.progression.training.TrainablePlayerStat.ENDURANCE;
import static com.sanguiwara.progression.training.TrainablePlayerStat.FINITION_AU_CERCLE;
import static com.sanguiwara.progression.training.TrainablePlayerStat.FLOATER;
import static com.sanguiwara.progression.training.TrainablePlayerStat.IQ;
import static com.sanguiwara.progression.training.TrainablePlayerStat.LANCER_FRANC;
import static com.sanguiwara.progression.training.TrainablePlayerStat.PASSING_SKILLS;
import static com.sanguiwara.progression.training.TrainablePlayerStat.PHYSIQUE;
import static com.sanguiwara.progression.training.TrainablePlayerStat.PROTECTION_CERCLE;
import static com.sanguiwara.progression.training.TrainablePlayerStat.SOLIDITE;
import static com.sanguiwara.progression.training.TrainablePlayerStat.SPEED;
import static com.sanguiwara.progression.training.TrainablePlayerStat.TIMING_BLOCK;
import static com.sanguiwara.progression.training.TrainablePlayerStat.TIR_2_PTS;
import static com.sanguiwara.progression.training.TrainablePlayerStat.TIR_3_PTS;

@Mapper(componentModel = "spring", uses = TemporaryModifierDTOMapper.class)
public interface TrainingProgressionDTOMapper {

    @Mapping(target = "statImpacts", expression = "java(mapStatImpacts(trainingProgression))")
    TrainingProgressionDTO toDto(TrainingProgression trainingProgression);

    default List<TrainingProgressionImpactDTO> mapStatImpacts(TrainingProgression trainingProgression) {
        if (trainingProgression == null) {
            return List.of();
        }

        TrainingType type = trainingProgression.type();
        return switch (type) {
            case SHOOTING -> List.of(
                    impact(TIR_3_PTS, THREE_POINT, 1, 2, 1.0),
                    impact(TIR_2_PTS, TWO_POINT, 1, 2, 1.0),
                    impact(LANCER_FRANC, FREE_THROW, 1, 2, 1.0),
                    impact(FLOATER, ProgressionSkillGroup.FLOATER, 1, 2, 1.0),
                    impact(FINITION_AU_CERCLE, DRIVE, 1, 2, 1.0)
            );
            case DEFENSE -> List.of(
                    impact(DEF_EXTERIEUR, PERIMETER_DEFENSE, 1, 2, 1.0),
                    impact(DEF_POSTE, INTERIOR_DEFENSE, 1, 2, 1.0),
                    impact(PROTECTION_CERCLE, RIM_PROTECTION, 1, 2, 1.0),
                    impact(TrainablePlayerStat.STEAL, ProgressionSkillGroup.STEAL, 1, 2, 1.0),
                    impact(TIMING_BLOCK, BLOCK, 1, 2, 1.0)
            );
            case PHYSICAL -> List.of(
                    impact(PHYSIQUE, ATHLETIC, 1, 3, 1.0),
                    impact(SPEED, ATHLETIC, 1, 3, 1.0),
                    impact(ENDURANCE, ATHLETIC, 1, 3, 1.0),
                    impact(SOLIDITE, ATHLETIC, 1, 3, 1.0)
            );
            case PLAYMAKING -> List.of(
                    impact(BALLHANDLING, DRIVE, 1, 3, 1.0),
                    impact(PASSING_SKILLS, PLAYMAKING, 1, 3, 1.0),
                    impact(BASKETBALL_IQ_OFF, MENTAL, 1, 3, 1.0),
                    impact(IQ, MENTAL, 1, 3, 1.0)
            );
            case MORALE -> List.of(
                    impact(TrainablePlayerStat.MORALE, ProgressionSkillGroup.MORALE, 1, 3, 1.0)
            );
            case TACTICAL -> List.of(
                    impact(BASKETBALL_IQ_OFF, MENTAL, 2, 3, 1.0),
                    impact(BASKETBALL_IQ_DEF, MENTAL, 2, 3, 1.0),
                    impact(IQ, MENTAL, 2, 3, 1.0)
            );
            case FREE_PLAY -> List.of(
                    impact(TIR_3_PTS, THREE_POINT, 1, 1, 0.35),
                    impact(TIR_2_PTS, TWO_POINT, 1, 1, 0.35),
                    impact(FINITION_AU_CERCLE, DRIVE, 1, 1, 0.35),
                    impact(BALLHANDLING, DRIVE, 1, 1, 0.35),
                    impact(PASSING_SKILLS, PLAYMAKING, 1, 1, 0.35),
                    impact(BASKETBALL_IQ_OFF, MENTAL, 1, 1, 0.35),
                    impact(DEF_EXTERIEUR, PERIMETER_DEFENSE, 1, 1, 0.35),
                    impact(DEF_POSTE, INTERIOR_DEFENSE, 1, 1, 0.35),
                    impact(PROTECTION_CERCLE, RIM_PROTECTION, 1, 1, 0.35),
                    impact(TrainablePlayerStat.STEAL, ProgressionSkillGroup.STEAL, 1, 1, 0.35),
                    impact(BASKETBALL_IQ_DEF, MENTAL, 1, 1, 0.35),
                    impact(TrainablePlayerStat.MORALE, ProgressionSkillGroup.MORALE, 1, 1, 1.0)
            );
            case THREE_POINT_FOCUS -> List.of();
        };
    }

    private static TrainingProgressionImpactDTO impact(
            TrainablePlayerStat stat,
            ProgressionSkillGroup skillGroup,
            int minDelta,
            int maxDelta,
            double progressionMultiplier
    ) {
        return new TrainingProgressionImpactDTO(
                stat,
                stat.playerField(),
                skillGroup,
                minDelta,
                maxDelta,
                progressionMultiplier
        );
    }
}
