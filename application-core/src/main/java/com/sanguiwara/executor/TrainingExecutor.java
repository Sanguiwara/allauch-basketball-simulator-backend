package com.sanguiwara.executor;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.Training;
import com.sanguiwara.baserecords.TrainingType;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingExecutor {

    private static final int MIN_SKILL_VALUE = 1;
    private static final int MAX_SKILL_VALUE = 99;

    private static final int MIN_MORALE = 1;
    private static final int MAX_MORALE = 100;

    private final TrainingRepository trainingRepository;
    private final PlayerRepository playerRepository;
    private final PlayerProgressionRepository playerProgressionRepository;

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
            progressionList.add(new PlayerProgression(after.getId(), ProgressionEventType.TRAINING, trainingId, delta));
        }

        playerProgressionRepository.saveAll(progressionList);
        log.info("Training {} executed", trainingId);
    }

    private static void applyTraining(TrainingType trainingType, Player player) {
        switch (trainingType) {
            case SHOOTING -> {
                player.setTir3Pts(clampSkill(player.getTir3Pts() + 1));
                player.setTir2Pts(clampSkill(player.getTir2Pts() + 1));
                player.setLancerFranc(clampSkill(player.getLancerFranc() + 1));
                player.setFloater(clampSkill(player.getFloater() + 1));
                player.setFinitionAuCercle(clampSkill(player.getFinitionAuCercle() + 1));
            }
            case DEFENSE -> {
                player.setDefExterieur(clampSkill(player.getDefExterieur() + 1));
                player.setDefPoste(clampSkill(player.getDefPoste() + 1));
                player.setProtectionCercle(clampSkill(player.getProtectionCercle() + 1));
                player.setSteal(clampSkill(player.getSteal() + 1));
                player.setTimingBlock(clampSkill(player.getTimingBlock() + 1));
            }
            case PHYSICAL -> {
                player.setPhysique(clampSkill(player.getPhysique() + 1));
                player.setSpeed(clampSkill(player.getSpeed() + 1));
                player.setEndurance(clampSkill(player.getEndurance() + 1));
                player.setSolidite(clampSkill(player.getSolidite() + 1));
            }
            case PLAYMAKING -> {
                player.setBallhandling(clampSkill(player.getBallhandling() + 1));
                player.setPassingSkills(clampSkill(player.getPassingSkills() + 1));
                player.setBasketballIqOff(clampSkill(player.getBasketballIqOff() + 1));
                player.setIq(clampSkill(player.getIq() + 1));
            }
            case MORALE -> player.setMorale(clampMorale(player.getMorale() + 2));
            case TACTICAL -> {
                player.setBasketballIqOff(clampSkill(player.getBasketballIqOff() + 1));
                player.setBasketballIqDef(clampSkill(player.getBasketballIqDef() + 1));
                player.setIq(clampSkill(player.getIq() + 1));
            }
        }
    }

    private static int clampSkill(int value) {
        return Math.clamp(value, MIN_SKILL_VALUE, MAX_SKILL_VALUE);
    }

    private static int clampMorale(int value) {
        return Math.clamp(value, MIN_MORALE, MAX_MORALE);
    }
}

