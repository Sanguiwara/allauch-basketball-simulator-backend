package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.GameResultSummary;
import com.sanguiwara.dto.GameResultDTO;
import com.sanguiwara.result.GameResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameResultDTOMapper {

    GameResultDTO toDto(GameResult gameResult);

    default GameResultDTO toDto(GameResultSummary summary) {
        if (summary == null) {
            return null;
        }

        return new GameResultDTO(
                new GameResultDTO.BoxScoreDTO(
                        new GameResultDTO.ShootingResultDTO(summary.homeThreePtAttempts(), summary.homeThreePtMade()),
                        new GameResultDTO.ShootingResultDTO(summary.homeDriveAttempts(), summary.homeDriveMade()),
                        new GameResultDTO.ShootingResultDTO(summary.homeTwoPtAttempts(), summary.homeTwoPtMade())
                ),
                new GameResultDTO.BoxScoreDTO(
                        new GameResultDTO.ShootingResultDTO(summary.awayThreePtAttempts(), summary.awayThreePtMade()),
                        new GameResultDTO.ShootingResultDTO(summary.awayDriveAttempts(), summary.awayDriveMade()),
                        new GameResultDTO.ShootingResultDTO(summary.awayTwoPtAttempts(), summary.awayTwoPtMade())
                )
        );
    }
}
