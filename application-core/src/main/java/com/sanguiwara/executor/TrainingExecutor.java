package com.sanguiwara.executor;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.Team;
import com.sanguiwara.baserecords.Training;
import com.sanguiwara.progression.PlayerProgression;
import com.sanguiwara.progression.PlayerProgressionDelta;
import com.sanguiwara.progression.ProgressionEventType;
import com.sanguiwara.progression.training.TrainingEngine;
import com.sanguiwara.repository.PlayerProgressionRepository;
import com.sanguiwara.repository.PlayerRepository;
import com.sanguiwara.repository.TrainingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingExecutor {

    private final TrainingRepository trainingRepository;
    private final PlayerRepository playerRepository;
    private final PlayerProgressionRepository playerProgressionRepository;
    private final TrainingEngine trainingEngine;

    @Transactional
    public void executeTraining(UUID trainingId) {
        Training training = trainingRepository.findById(trainingId).orElseThrow();
        Team team = training.getTeam();
        if (team == null) {
            log.warn("Training execution skipped trainingId={} type={} executeAt={} reason=no_team",
                    trainingId,
                    training.getTrainingType(),
                    training.getExecuteAt());
            return;
        }

        List<Player> players = team.getPlayers();
        if (players == null || players.isEmpty()) {
            log.warn("Training execution skipped trainingId={} type={} executeAt={} teamId={} teamName={} reason=no_players",
                    trainingId,
                    training.getTrainingType(),
                    training.getExecuteAt(),
                    team.getId(),
                    team.getName());
            return;
        }

        log.info("Training execution started trainingId={} type={} executeAt={} teamId={} teamName={} playerCount={}",
                trainingId,
                training.getTrainingType(),
                training.getExecuteAt(),
                team.getId(),
                team.getName(),
                players.size());

        Map<UUID, Player> beforeByPlayerId = new HashMap<>(players.size());
        for (Player player : players) {
            beforeByPlayerId.put(player.getId(), player.snapshotPlayer());
        }

        for (Player player : players) {
            trainingEngine.applyTraining(training.getTrainingProgression(), player);
            playerRepository.save(player);
        }

        List<PlayerProgression> progressionList = new ArrayList<>(players.size());
        int badgesAddedCount = 0;
        int temporaryModifiersAddedCount = 0;
        for (Player after : players) {
            Player before = beforeByPlayerId.get(after.getId());
            PlayerProgressionDelta delta = PlayerProgressionDelta.between(before, after);
            badgesAddedCount += delta.badgesAdded().size();
            temporaryModifiersAddedCount += delta.temporaryModifiersAdded().size();

            log.debug("Training player progression trainingId={} playerId={} playerName={} delta={} badgesAdded={} temporaryModifiersAdded={}",
                    trainingId,
                    after.getId(),
                    after.getName(),
                    delta,
                    delta.badgesAdded(),
                    delta.temporaryModifiersAdded());

            // Store only badges earned during this event (not the full post-event badge set).
            progressionList.add(new PlayerProgression(
                    after.getId(),
                    ProgressionEventType.TRAINING,
                    trainingId,
                    delta.badgesAdded(),
                    delta.temporaryModifiersAdded(),
                    delta
            ));
        }

        playerProgressionRepository.saveAll(progressionList);
        log.info("Training execution completed trainingId={} type={} teamId={} playerCount={} progressionCount={} badgesAddedCount={} temporaryModifiersAddedCount={}",
                trainingId,
                training.getTrainingType(),
                team.getId(),
                players.size(),
                progressionList.size(),
                badgesAddedCount,
                temporaryModifiersAddedCount);
    }
}
