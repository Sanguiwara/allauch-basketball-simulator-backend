package com.sanguiwara.service;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.MatchupAttacker;
import com.sanguiwara.baserecords.MatchupDefender;
import com.sanguiwara.baserecords.Matchups;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.Position;
import com.sanguiwara.baserecords.Team;
import com.sanguiwara.repository.ClubRepository;
import com.sanguiwara.repository.GamePlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class GamePlanServiceImpl implements GamePlanService {

    private final GamePlanRepository gamePlanRepository;
    private final ClubRepository clubRepository;

    private static final int TOTAL_TEAM_MINUTES = 200;
    private static final int EXPECTED_STARTERS = 5;
    private static final double SHOT_SHARE_SUM_TOLERANCE = 0.0001;

    @Override
    public Optional<GamePlan> getGamePlan(UUID id) {
        return gamePlanRepository.findById(id).map(this::refreshScoresIfNeeded);

    }

    @Override
    public Optional<GamePlan> getNextUpcomingGamePlanForClub(UUID clubId) {
        return gamePlanRepository.findNextUpcomingGamePlanForClub(clubId).map(this::refreshScoresIfNeeded);
    }

    @Override
    public Optional<GamePlan> getNextUpcomingGamePlanForAUserSub(String sub) {
        return clubRepository.findByUserSub(sub)
                .flatMap(club -> getNextUpcomingGamePlanForClub(club.getId()));
    }

    @Override
    public GamePlan update(GamePlan gamePlan) {
        ensureGamePlanCanBeUpdated(gamePlan);
        return persistGamePlan(gamePlan);
    }

    @Override
    public List<GamePlan> saveAndApplyToUpcomingGamePlans(GamePlan gamePlan) {
        ensureGamePlanCanBeUpdated(gamePlan);

        GamePlan persistedSource = gamePlanRepository.findById(gamePlan.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game plan not found"));
        Team ownerTeam = persistedSource.getOwnerTeam();
        UUID ownerTeamId = requireOwnerTeamId(ownerTeam);

        validateSourceTeam(gamePlan, ownerTeam);
        validateTemplate(gamePlan, ownerTeam);

        GamePlan savedSource = persistGamePlan(gamePlan);
        List<GamePlan> upcomingGamePlans = gamePlanRepository.findUpcomingUnplayedGamePlansForTeam(ownerTeamId, Instant.now());
        List<GamePlan> updatedGamePlans = new ArrayList<>();

        for (GamePlan targetGamePlan : upcomingGamePlans) {
            if (savedSource.getId().equals(targetGamePlan.getId())) {
                continue;
            }

            applyTemplateWithoutPositions(gamePlan, targetGamePlan);
            targetGamePlan.recalculateInGamePlayerScores();
            GamePlan savedTarget = gamePlanRepository.update(targetGamePlan);

            if (hasPositions(gamePlan)) {
                applyTemplatePositions(gamePlan, savedTarget);
                savedTarget.recalculateInGamePlayerScores();
                savedTarget = gamePlanRepository.update(savedTarget);
            }

            updatedGamePlans.add(savedTarget);
        }

        log.info(
                "Applied GamePlan template sourceGamePlanId={} ownerTeamId={} updatedGamePlanCount={}",
                savedSource.getId(),
                ownerTeamId,
                updatedGamePlans.size()
        );
        return updatedGamePlans;
    }


    @Override
    public GamePlan generateGamePlan(Team t1, Team t2) {


        GamePlan gameplan =  new GamePlan(null, t1, t2);
        gameplan = gamePlanRepository.save(gameplan);
        GamePlan finalHomeGamePlan = gameplan;
        List<InGamePlayer> activePlayers = gameplan.getOwnerTeam().getPlayers().stream()
                .limit(10)
                .map(player -> new InGamePlayer(player, finalHomeGamePlan.getId())) // map Player -> InGamePlayer
                .toList();
        gameplan.setActivePlayers(activePlayers);
        return update(gameplan);


    }

    private void ensureGamePlanCanBeUpdated(GamePlan gamePlan) {
        if (gamePlan == null || gamePlan.getId() == null) {
            throw badRequest("Game plan id is required");
        }

        if (gamePlanRepository.isGameFinished(gamePlan.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Game plan can no longer be updated because the match is finished"
            );
        }
    }

    private GamePlan persistGamePlan(GamePlan gamePlan) {
        gamePlan.recalculateInGamePlayerScores();

        // Build log payload before persistence so we log the user's intended state
        // with resolved names (DTO mapper resolves matchup player IDs to Players).
        String gamePlanDescription = describeGamePlan(gamePlan);
        GamePlan saved = gamePlanRepository.update(gamePlan);
        log.info("Saved GamePlan:\n{}", gamePlanDescription);
        return saved;
    }

    private static void validateSourceTeam(GamePlan sourceGamePlan, Team persistedOwnerTeam) {
        if (sourceGamePlan.getOwnerTeam() == null) {
            return;
        }

        UUID sourceOwnerTeamId = sourceGamePlan.getOwnerTeam().getId();
        UUID persistedOwnerTeamId = requireOwnerTeamId(persistedOwnerTeam);
        if (!persistedOwnerTeamId.equals(sourceOwnerTeamId)) {
            throw badRequest("Game plan owner team cannot be changed");
        }
    }

    private static void validateTemplate(GamePlan template, Team ownerTeam) {
        validateShotShares(template);

        List<InGamePlayer> activePlayers = template.getActivePlayers();
        if (activePlayers == null || activePlayers.isEmpty()) {
            throw badRequest("activePlayers is required");
        }

        Set<UUID> activePlayerIds = new HashSet<>();
        int totalMinutes = 0;
        int starters = 0;

        for (InGamePlayer activePlayer : activePlayers) {
            if (activePlayer == null || activePlayer.getPlayer() == null || activePlayer.getPlayer().getId() == null) {
                throw badRequest("Each active player must reference a player id");
            }

            totalMinutes += activePlayer.getMinutesPlayed();
            if (activePlayer.isStarter()) {
                starters++;
            }
        }

        if (totalMinutes != TOTAL_TEAM_MINUTES) {
            throw badRequest("activePlayers minutesPlayed must sum to " + TOTAL_TEAM_MINUTES + ", got=" + totalMinutes);
        }
//        if (starters != EXPECTED_STARTERS) {
//            throw badRequest("activePlayers must contain exactly " + EXPECTED_STARTERS + " starters, got=" + starters);
//        }

        validatePositions(template, activePlayerIds);
    }

    private static void validateShotShares(GamePlan template) {
        validateShare("threePointAttemptShare", template.getThreePointAttemptShare());
        validateShare("midRangeAttemptShare", template.getMidRangeAttemptShare());
        validateShare("driveAttemptShare", template.getDriveAttemptShare());

        double sum = template.getThreePointAttemptShare()
                + template.getMidRangeAttemptShare()
                + template.getDriveAttemptShare();
        if (Math.abs(sum - 1.0) > SHOT_SHARE_SUM_TOLERANCE) {
            throw badRequest("Shot attempt shares must sum to 1.0, got=" + sum);
        }
    }

    private static void validateShare(String fieldName, double value) {
        if (value < 0.0 || value > 1.0) {
            throw badRequest(fieldName + " must be in [0.0, 1.0], got=" + value);
        }
    }

    private static void validatePositions(GamePlan template, Set<UUID> activePlayerIds) {
        if (!hasPositions(template)) {
            return;
        }

        Set<UUID> positionedPlayerIds = new HashSet<>();
        for (Map.Entry<Position, InGamePlayer> entry : template.getPositions().entrySet()) {
            InGamePlayer positionedPlayer = entry.getValue();
            if (positionedPlayer == null || positionedPlayer.getPlayer() == null || positionedPlayer.getPlayer().getId() == null) {
                throw badRequest("Position " + entry.getKey() + " must reference an active player id");
            }

            UUID playerId = positionedPlayer.getPlayer().getId();
            if (!activePlayerIds.contains(playerId)) {
                throw badRequest("Position " + entry.getKey() + " references a player who is not active: " + playerId);
            }
            if (!positionedPlayerIds.add(playerId)) {
                throw badRequest("Player " + playerId + " cannot be assigned to multiple positions");
            }
        }
    }



    private static UUID requireOwnerTeamId(Team ownerTeam) {
        if (ownerTeam == null || ownerTeam.getId() == null) {
            throw badRequest("Game plan owner team is required");
        }
        return ownerTeam.getId();
    }

    private static void applyTemplateWithoutPositions(GamePlan template, GamePlan target) {
        Map<UUID, UUID> existingInGamePlayerIdsByPlayerId = inGamePlayerIdsByPlayerId(target.getActivePlayers());
        List<InGamePlayer> copiedActivePlayers = template.getActivePlayers()
                .stream()
                .map(sourcePlayer -> copyInGamePlayerTemplate(
                        sourcePlayer,
                        target.getId(),
                        existingInGamePlayerIdsByPlayerId.get(sourcePlayer.getPlayer().getId())
                ))
                .toList();

        target.setActivePlayers(copiedActivePlayers);
        target.setPositions(new HashMap<>());
        target.setThreePointAttemptShare(template.getThreePointAttemptShare());
        target.setMidRangeAttemptShare(template.getMidRangeAttemptShare());
        target.setDriveAttemptShare(template.getDriveAttemptShare());
    }

    private static void applyTemplatePositions(GamePlan template, GamePlan target) {
        Map<UUID, InGamePlayer> targetPlayersByPlayerId = inGamePlayersByPlayerId(target.getActivePlayers());
        Map<Position, InGamePlayer> copiedPositions = new HashMap<>();

        template.getPositions().forEach((position, sourceInGamePlayer) -> {
            UUID playerId = sourceInGamePlayer.getPlayer().getId();
            InGamePlayer targetInGamePlayer = targetPlayersByPlayerId.get(playerId);
            if (targetInGamePlayer == null) {
                throw new IllegalStateException("Saved target game plan is missing active player " + playerId);
            }
            copiedPositions.put(position, targetInGamePlayer);
        });

        target.setPositions(copiedPositions);
    }

    private static InGamePlayer copyInGamePlayerTemplate(InGamePlayer source, UUID targetGamePlanId, UUID targetInGamePlayerId) {
        InGamePlayer copy = new InGamePlayer(source.getPlayer(), targetGamePlanId);
        copy.setId(targetInGamePlayerId);
        copy.setStarter(source.isStarter());
        copy.setMinutesPlayed(source.getMinutesPlayed());
        copy.setUsageShoot(source.getUsageShoot());
        copy.setUsageDrive(source.getUsageDrive());
        copy.setUsagePost(source.getUsagePost());
        return copy;
    }

    private static Map<UUID, UUID> inGamePlayerIdsByPlayerId(List<InGamePlayer> activePlayers) {
        Map<UUID, UUID> idsByPlayerId = new HashMap<>();
        if (activePlayers == null) {
            return idsByPlayerId;
        }

        for (InGamePlayer activePlayer : activePlayers) {
            if (activePlayer != null && activePlayer.getPlayer() != null && activePlayer.getPlayer().getId() != null) {
                idsByPlayerId.put(activePlayer.getPlayer().getId(), activePlayer.getId());
            }
        }
        return idsByPlayerId;
    }

    private static Map<UUID, InGamePlayer> inGamePlayersByPlayerId(List<InGamePlayer> activePlayers) {
        Map<UUID, InGamePlayer> playersByPlayerId = new HashMap<>();
        if (activePlayers == null) {
            return playersByPlayerId;
        }

        for (InGamePlayer activePlayer : activePlayers) {
            if (activePlayer != null && activePlayer.getPlayer() != null && activePlayer.getPlayer().getId() != null) {
                playersByPlayerId.put(activePlayer.getPlayer().getId(), activePlayer);
            }
        }
        return playersByPlayerId;
    }

    private static boolean hasPositions(GamePlan gamePlan) {
        return gamePlan.getPositions() != null && !gamePlan.getPositions().isEmpty();
    }

    private static ResponseStatusException badRequest(String reason) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, reason);
    }

    private GamePlan refreshScoresIfNeeded(GamePlan gamePlan) {
        if (gamePlanRepository.isGameFinished(gamePlan.getId())) {
            return gamePlan;
        }

        gamePlan.recalculateInGamePlayerScores();
        GamePlan savedGamePlan = gamePlanRepository.update(gamePlan);
        log.debug("Recalculated in-game player scores for gamePlanId={}", gamePlan.getId());
        return savedGamePlan;
    }

    private static String describeGamePlan(GamePlan gamePlan) {
        if (gamePlan == null) return "<null gamePlan>";

        StringBuilder descriptionBuilder = new StringBuilder(512);
        descriptionBuilder.append("id=").append(gamePlan.getId()).append('\n');
        descriptionBuilder.append("ownerTeam=").append(teamLabel(gamePlan.getOwnerTeam())).append('\n');
        descriptionBuilder.append("opponentTeam=").append(teamLabel(gamePlan.getOpponentTeam())).append('\n');
        descriptionBuilder.append("defenseType=").append(gamePlan.getDefenseType()).append('\n');
        descriptionBuilder.append("shotShare(3pt,mid,drive)=")
                .append(gamePlan.getThreePointAttemptShare()).append(',')
                .append(gamePlan.getMidRangeAttemptShare()).append(',')
                .append(gamePlan.getDriveAttemptShare()).append('\n');
        descriptionBuilder.append("totalShotNumber=").append(gamePlan.getTotalShotNumber()).append('\n');
        descriptionBuilder.append("blockScore=").append(gamePlan.getBlockScore())
                .append(" blockProbability=").append(gamePlan.getBlockProbability())
                .append(" assistProbability=").append(gamePlan.getAssistProbability())
                .append('\n');

        // Active players
        List<InGamePlayer> activePlayers = gamePlan.getActivePlayers();
        if (activePlayers == null) {
            descriptionBuilder.append("activePlayers=<null>\n");
        } else {
            var sortedActivePlayers = activePlayers.stream()
                    .sorted(Comparator
                            .comparing(InGamePlayer::isStarter).reversed()
                            .thenComparing(p -> safeName(p.getPlayer()))
                            .thenComparing(p -> safeUuid(p.getPlayer())))
                    .toList();
            descriptionBuilder.append("activePlayers(count=").append(sortedActivePlayers.size()).append(")=").append('\n');
            for (var activePlayer : sortedActivePlayers) {
                descriptionBuilder.append(" - ")
                        .append(inGamePlayerLabel(activePlayer))
                        .append('\n');
            }
        }

        // Positions
        Map<Position, InGamePlayer> positions = gamePlan.getPositions();
        if (positions == null) {
            descriptionBuilder.append("positions=<null>\n");
        } else {
            descriptionBuilder.append("positions(count=").append(positions.size()).append(")=").append('\n');
            positions.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> descriptionBuilder.append(" - ")
                            .append(entry.getKey())
                            .append(": ")
                            .append(inGamePlayerLabel(entry.getValue()))
                            .append('\n'));
        }

        // Matchups
        Matchups matchups = gamePlan.getMatchups();
        if (matchups == null) {
            descriptionBuilder.append("matchups=<null>\n");
        } else {
            descriptionBuilder.append("matchups(count=").append(matchups.size()).append(")=").append('\n');
            matchups.asMap().entrySet().stream()
                    .sorted(Comparator
                            .comparing((Map.Entry<MatchupDefender, MatchupAttacker> e) -> safeName(e.getKey().player()))
                            .thenComparing(e -> safeUuid(e.getKey().player())))
                    .forEach(entry -> descriptionBuilder.append(" - ")
                            .append("defender=")
                            .append(playerLabel(entry.getKey().player()))
                            .append(" -> ")
                            .append("attacker=")
                            .append(playerLabel(entry.getValue().player()))
                            .append('\n'));
        }

        return descriptionBuilder.toString().trim();
    }

    private static String teamLabel(Team t) {
        if (t == null) return "<null team>";
        return "name=" + safe(t.getName())
                + " id=" + t.getId()
                + " clubId=" + t.getClubID()
                + " category=" + t.getCategory()
                + " gender=" + t.getGender();
    }

    private static String inGamePlayerLabel(InGamePlayer inGamePlayer) {
        if (inGamePlayer == null) return "<null inGamePlayer>";
        return "player=" + playerLabel(inGamePlayer.getPlayer())
                + " inGamePlayerId=" + inGamePlayer.getId()
                + " starter=" + inGamePlayer.isStarter()
                + " minutesPlayed=" + inGamePlayer.getMinutesPlayed()
                + " usage(shoot,drive,post)=" + inGamePlayer.getUsageShoot() + "," + inGamePlayer.getUsageDrive() + "," + inGamePlayer.getUsagePost()
                + " box(points,assists,reb,stl,blk)="
                + inGamePlayer.getPoints() + "," + inGamePlayer.getAssists() + "," + (inGamePlayer.getOffensiveRebounds() + inGamePlayer.getDefensiveRebounds())
                + "," + inGamePlayer.getSteals() + "," + inGamePlayer.getBlocks();
    }

    private static String playerLabel(Player player) {
        if (player == null) return "<null player>";
        return safe(player.getName()) + " (" + player.getId() + ")";
    }

    private static String safe(String s) {
        return (s == null || s.isBlank()) ? "<unknown>" : s;
    }

    private static String safeName(Player player) {
        return player == null ? "" : (player.getName() == null ? "" : player.getName());
    }

    private static UUID safeUuid(Player player) {
        return player == null ? null : player.getId();
    }

}
