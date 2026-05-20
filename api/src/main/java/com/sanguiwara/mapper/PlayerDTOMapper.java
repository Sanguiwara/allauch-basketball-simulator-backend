package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.dto.PlayerDTO;
import com.sanguiwara.dto.PlayerScoresDTO;
import com.sanguiwara.calculator.PlayerScoreCalculator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BadgeDTOMapper.class, TemporaryModifierDTOMapper.class})
public interface PlayerDTOMapper {

    @Mapping(target = "clubId", source = "clubID")
    @Mapping(target = "teamIds", source = "teamsID")
    @Mapping(target = "badges", source = "badgeIds")
    @Mapping(target = "temporaryModifiers", source = "temporaryModifiers")
    @Mapping(target = "scores", expression = "java(mapScores(player))")
    PlayerDTO toDto(Player player);

    @Mapping(target = "clubID", source = "clubId")
    @Mapping(target = "teamsID", source = "teamIds")
    @Mapping(target = "badgeIds", source = "badges")
    @Mapping(target = "temporaryModifiers", ignore = true)
    @Mapping(target = "injured", ignore = true)
    Player toDomain(PlayerDTO playerDTO);

    default PlayerScoresDTO mapScores(Player player) {
        if (player == null) {
            return null;
        }
        return new PlayerScoresDTO(
                PlayerScoreCalculator.calculateThreePtScore(player),
                PlayerScoreCalculator.calculateThreePtDefenseScore(player),
                PlayerScoreCalculator.calculateTwoPtScore(player),
                PlayerScoreCalculator.calculateTwoPtDefenseScore(player),
                PlayerScoreCalculator.calculateDriveScore(player),
                PlayerScoreCalculator.calculateDriveDefenseScore(player),
                PlayerScoreCalculator.calculateManToManPlaymakingOffScore(player),
                PlayerScoreCalculator.calculateManToManPlaymakingDefScore(player),
                PlayerScoreCalculator.calculateZonePlaymakingOffScore(player),
                PlayerScoreCalculator.calculateZonePlaymakingDefScore(player),
                PlayerScoreCalculator.calculateZone23DefenseScore(player),
                PlayerScoreCalculator.calculateZone32DefenseScore(player),
                PlayerScoreCalculator.calculateZone212DefenseScore(player),
                PlayerScoreCalculator.calculateReboundScore(player),
                PlayerScoreCalculator.calculateStealScore(player)
        );
    }
}
