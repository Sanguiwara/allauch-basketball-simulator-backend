package com.sanguiwara.service;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.Position;
import com.sanguiwara.baserecords.Team;
import com.sanguiwara.repository.ClubRepository;
import com.sanguiwara.repository.GamePlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class GamePlanServiceImpl implements GamePlanService {

    private final GamePlanRepository gamePlanRepository;
    private final ClubRepository clubRepository;


    @Override
    public Optional<GamePlan> getGamePlan(UUID id) {
        return gamePlanRepository.findById(id);

    }

    @Override
    public Optional<GamePlan> getNextUpcomingGamePlanForClub(UUID clubId) {
        return gamePlanRepository.findNextUpcomingGamePlanForClub(clubId);
    }

    @Override
    public Optional<GamePlan> getNextUpcomingGamePlanForAUserSub(String sub) {
        return clubRepository.findByUserSub(sub)
                .flatMap(club -> getNextUpcomingGamePlanForClub(club.getId()));
    }

    @Override
    public GamePlan update(GamePlan gamePlan) {
        // Build log payload before persistence so we log the user's intended state
        // with resolved names (DTO mapper resolves matchup player IDs to Players).
        String gamePlanDescription = describeGamePlan(gamePlan);
        GamePlan saved = gamePlanRepository.update(gamePlan);
        log.info("Saved GamePlan:\n{}", gamePlanDescription);
        return saved;
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
        return gamePlanRepository.update(gameplan);


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
        Map<Player, Player> matchups = gamePlan.getMatchups();
        if (matchups == null) {
            descriptionBuilder.append("matchups=<null>\n");
        } else {
            descriptionBuilder.append("matchups(count=").append(matchups.size()).append(")=").append('\n');
            matchups.entrySet().stream()
                    .sorted(Comparator
                            .comparing((Map.Entry<Player, Player> e) -> safeName(e.getKey()))
                            .thenComparing(e -> safeUuid(e.getKey())))
                    .forEach(entry -> descriptionBuilder.append(" - ")
                            .append(playerLabel(entry.getKey()))
                            .append(" -> ")
                            .append(playerLabel(entry.getValue()))
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
