package com.sanguiwara.executor;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.Training;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.progression.manager.TrainingProgressionManager;
import com.sanguiwara.progression.PlayerProgression;
import com.sanguiwara.progression.PlayerProgressionDelta;
import com.sanguiwara.progression.ProgressionEventType;
import com.sanguiwara.repository.PlayerProgressionRepository;
import com.sanguiwara.repository.PlayerRepository;
import com.sanguiwara.repository.TrainingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingExecutor {

    private final TrainingRepository trainingRepository;
    private final PlayerRepository playerRepository;
    private final PlayerProgressionRepository playerProgressionRepository;
    private final TrainingProgressionManager trainingProgressionManager;

    @Transactional
    public void executeTraining(UUID trainingId) {
        log.info("Executing training {}", trainingId);
        Training training = trainingRepository.findById(trainingId).orElseThrow();
        if (training.getTeam() == null || training.getTeam().getPlayers() == null) {
            return;
        }

        List<Player> players = training.getTeam().getPlayers();
        Map<UUID, Player> beforeByPlayerId = new HashMap<>(players.size());
        for (Player player : players) {
            beforeByPlayerId.put(player.getId(), player.snapshotPlayer());
        }

        for (Player player : players) {
            applyTraining(training.getTrainingType(), player);
            playerRepository.save(player);
        }

        List<PlayerProgression> progressionList = new ArrayList<>(players.size());
        for (Player after : players) {
            Player before = beforeByPlayerId.get(after.getId());
            PlayerProgressionDelta delta = PlayerProgressionDelta.between(before, after);
            var badgeIds = Set.copyOf(after.getBadgeIds());
            progressionList.add(new PlayerProgression(after.getId(), ProgressionEventType.TRAINING, trainingId, badgeIds, delta));
        }

        playerProgressionRepository.saveAll(progressionList);
        log.info("Training {} executed", trainingId);
    }

    private void applyTraining(TrainingType trainingType, Player player) {
        trainingProgressionManager.applyTraining(trainingType, player);
    }
}
