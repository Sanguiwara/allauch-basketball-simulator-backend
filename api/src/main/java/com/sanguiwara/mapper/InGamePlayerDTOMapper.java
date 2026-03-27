package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.dto.InGamePlayerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PlayerDTOMapper.class})
public interface InGamePlayerDTOMapper {

    // UI-only fields: we intentionally duplicate the scoring formulas from domain ShotSpecs here.
    // This avoids coupling the API DTO layer to domain calculator classes.

    // From ThreePointSpecification#getPlayerScoreForAShot
    double THREE_SCORE_SPEED_WEIGHT_OFF = 0.10;
    double THREE_SCORE_SIZE_WEIGHT_OFF = 0.15;
    double THREE_SCORE_ENDURANCE_WEIGHT_OFF = 0.10;
    double THREE_SCORE_RATING_WEIGHT_OFF = 0.50;
    double THREE_SCORE_IQ_WEIGHT_OFF = 0.15;

    // From TwoPointSpecification#getPlayerScoreForAShot
    double TWO_OFF_SPEED_COEFF = 0.08;
    double TWO_OFF_SIZE_COEFF = 0.22;
    double TWO_OFF_ENDURANCE_COEFF = 0.12;
    double TWO_OFF_BALLHANDLING_COEFF = 0.15;
    double TWO_OFF_FINISH_AT_RIM_COEFF = 0.28;
    double TWO_OFF_IQ_COEFF = 0.15;

    // From DriveSpecification#getPlayerScoreForAShot
    double DRIVE_OFF_SPEED_WEIGHT = 0.18;
    double DRIVE_OFF_SIZE_WEIGHT = 0.08;
    double DRIVE_OFF_ENDURANCE_WEIGHT = 0.05;
    double DRIVE_OFF_BALLHANDLING_WEIGHT = 0.20;
    double DRIVE_OFF_FINITION_WEIGHT = 0.35;
    double DRIVE_OFF_FLOATER_WEIGHT = 0.10;
    double DRIVE_OFF_IQ_WEIGHT = 0.04;

    @Mapping(target = "threePtScore", expression = "java(threePtScore(inGamePlayer))")
    @Mapping(target = "twoPtScore", expression = "java(twoPtScore(inGamePlayer))")
    @Mapping(target = "driveScore", expression = "java(driveScore(inGamePlayer))")
    InGamePlayerDTO toDto(InGamePlayer inGamePlayer);

    InGamePlayer toDomain(InGamePlayerDTO inGamePlayerDTO);

    default double threePtScore(InGamePlayer inGamePlayer) {
        Player attacker = inGamePlayer.getPlayer();
        return THREE_SCORE_SPEED_WEIGHT_OFF * attacker.getSpeed()
                + THREE_SCORE_SIZE_WEIGHT_OFF * attacker.getSize()
                + THREE_SCORE_ENDURANCE_WEIGHT_OFF * attacker.getEndurance()
                + THREE_SCORE_RATING_WEIGHT_OFF * attacker.getTir3Pts()
                + THREE_SCORE_IQ_WEIGHT_OFF * attacker.getBasketballIqOff();
    }

    default double twoPtScore(InGamePlayer inGamePlayer) {
        Player attacker = inGamePlayer.getPlayer();
        return TWO_OFF_SPEED_COEFF * attacker.getSpeed()
                + TWO_OFF_SIZE_COEFF * attacker.getSize()
                + TWO_OFF_ENDURANCE_COEFF * attacker.getEndurance()
                + TWO_OFF_BALLHANDLING_COEFF * attacker.getBallhandling()
                + TWO_OFF_FINISH_AT_RIM_COEFF * attacker.getFinitionAuCercle()
                + TWO_OFF_IQ_COEFF * attacker.getBasketballIqOff();
    }

    default double driveScore(InGamePlayer inGamePlayer) {
        Player attacker = inGamePlayer.getPlayer();
        return DRIVE_OFF_SPEED_WEIGHT * attacker.getSpeed()
                + DRIVE_OFF_SIZE_WEIGHT * attacker.getSize()
                + DRIVE_OFF_ENDURANCE_WEIGHT * attacker.getEndurance()
                + DRIVE_OFF_BALLHANDLING_WEIGHT * attacker.getBallhandling()
                + DRIVE_OFF_FINITION_WEIGHT * attacker.getFinitionAuCercle()
                + DRIVE_OFF_FLOATER_WEIGHT * attacker.getFloater()
                + DRIVE_OFF_IQ_WEIGHT * attacker.getBasketballIqOff();
    }
}

