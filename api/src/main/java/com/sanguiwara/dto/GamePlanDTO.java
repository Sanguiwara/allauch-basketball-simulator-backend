package com.sanguiwara.dto;

import com.sanguiwara.baserecords.DefenseType;
import com.sanguiwara.baserecords.Position;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record GamePlanDTO(UUID id, TeamDTO ownerTeam, TeamDTO opponentTeam, List<InGamePlayerDTO> activePlayers,
                          Map<UUID, UUID> matchups,
                          Map<Position, InGamePlayerDTO> positions, double threePointAttemptShare,
                          double midRangeAttemptShare, double driveAttemptShare, double totalShotNumber,
                          DefenseType defenseType) {

}
