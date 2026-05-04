package com.sanguiwara.calculator;

import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;

public final class PlayerScoreCalculator {
    private static final double THREE_PT_SCORE_SPEED_WEIGHT = 0.10;
    private static final double THREE_PT_SCORE_SIZE_WEIGHT = 0.15;
    private static final double THREE_PT_SCORE_ENDURANCE_WEIGHT = 0.10;
    private static final double THREE_PT_SCORE_RATING_WEIGHT = 0.50;
    private static final double THREE_PT_SCORE_IQ_WEIGHT = 0.15;

    private static final double THREE_PT_DEFENSE_SCORE_SPEED_WEIGHT = 0.10;
    private static final double THREE_PT_DEFENSE_SCORE_SIZE_WEIGHT = 0.10;
    private static final double THREE_PT_DEFENSE_SCORE_DEF_EXTERIEUR_WEIGHT = 0.65;
    private static final double THREE_PT_DEFENSE_SCORE_ENDURANCE_WEIGHT = 0.05;
    private static final double THREE_PT_DEFENSE_SCORE_IQ_WEIGHT = 0.10;

    private static final double TWO_PT_SCORE_SPEED_WEIGHT = 0.08;
    private static final double TWO_PT_SCORE_SIZE_WEIGHT = 0.22;
    private static final double TWO_PT_SCORE_ENDURANCE_WEIGHT = 0.12;
    private static final double TWO_PT_SCORE_FINISH_WEIGHT = 0.15;
    private static final double TWO_PT_SCORE_RATING_WEIGHT = 0.28;
    private static final double TWO_PT_SCORE_IQ_WEIGHT = 0.15;

    private static final double TWO_PT_DEFENSE_SCORE_SPEED_WEIGHT = 0.10;
    private static final double TWO_PT_DEFENSE_SCORE_SIZE_WEIGHT = 0.28;
    private static final double TWO_PT_DEFENSE_SCORE_ENDURANCE_WEIGHT = 0.12;
    private static final double TWO_PT_DEFENSE_SCORE_IQ_WEIGHT = 0.18;
    private static final double TWO_PT_DEFENSE_SCORE_STEAL_WEIGHT = 0.05;
    private static final double TWO_PT_DEFENSE_SCORE_DEF_POSTE_WEIGHT = 0.27;

    private static final double DRIVE_SCORE_SPEED_WEIGHT = 0.18;
    private static final double DRIVE_SCORE_SIZE_WEIGHT = 0.08;
    private static final double DRIVE_SCORE_ENDURANCE_WEIGHT = 0.05;
    private static final double DRIVE_SCORE_BALLHANDLING_WEIGHT = 0.20;
    private static final double DRIVE_SCORE_FINISH_WEIGHT = 0.35;
    private static final double DRIVE_SCORE_FLOATER_WEIGHT = 0.10;
    private static final double DRIVE_SCORE_IQ_WEIGHT = 0.04;

    private static final double DRIVE_DEFENSE_SCORE_SPEED_WEIGHT = 0.18;
    private static final double DRIVE_DEFENSE_SCORE_SIZE_WEIGHT = 0.22;
    private static final double DRIVE_DEFENSE_SCORE_DEF_EXTERIEUR_WEIGHT = 0.22;
    private static final double DRIVE_DEFENSE_SCORE_ENDURANCE_WEIGHT = 0.10;
    private static final double DRIVE_DEFENSE_SCORE_IQ_WEIGHT = 0.12;
    private static final double DRIVE_DEFENSE_SCORE_STEAL_WEIGHT = 0.10;
    private static final double DRIVE_DEFENSE_SCORE_DEF_POSTE_WEIGHT = 0.06;

    private static final double MAN_TO_MAN_PLAYMAKING_OFF_SPEED_WEIGHT = 0.15;
    private static final double MAN_TO_MAN_PLAYMAKING_OFF_SIZE_WEIGHT = 0.05;
    private static final double MAN_TO_MAN_PLAYMAKING_OFF_ENDURANCE_WEIGHT = 0.05;
    private static final double MAN_TO_MAN_PLAYMAKING_OFF_PASSING_WEIGHT = 0.20;
    private static final double MAN_TO_MAN_PLAYMAKING_OFF_IQ_WEIGHT = 0.25;
    private static final double MAN_TO_MAN_PLAYMAKING_OFF_HANDLING_WEIGHT = 0.15;
    private static final double MAN_TO_MAN_PLAYMAKING_OFF_THREE_PT_WEIGHT = 0.05;
    private static final double MAN_TO_MAN_PLAYMAKING_OFF_TWO_PT_WEIGHT = 0.05;
    private static final double MAN_TO_MAN_PLAYMAKING_OFF_FINISH_WEIGHT = 0.03;
    private static final double MAN_TO_MAN_PLAYMAKING_OFF_FLOATER_WEIGHT = 0.02;

    private static final double MAN_TO_MAN_PLAYMAKING_DEF_SPEED_WEIGHT = 0.15;
    private static final double MAN_TO_MAN_PLAYMAKING_DEF_SIZE_WEIGHT = 0.08;
    private static final double MAN_TO_MAN_PLAYMAKING_DEF_EXTERIOR_WEIGHT = 0.42;
    private static final double MAN_TO_MAN_PLAYMAKING_DEF_ENDURANCE_WEIGHT = 0.05;
    private static final double MAN_TO_MAN_PLAYMAKING_DEF_IQ_WEIGHT = 0.15;
    private static final double MAN_TO_MAN_PLAYMAKING_DEF_STEAL_WEIGHT = 0.15;

    private static final double ZONE_PLAYMAKING_OFF_PASSING_WEIGHT = 0.30;
    private static final double ZONE_PLAYMAKING_OFF_IQ_WEIGHT = 0.35;
    private static final double ZONE_PLAYMAKING_OFF_BALLHANDLING_WEIGHT = 0.10;
    private static final double ZONE_PLAYMAKING_OFF_COACHABILITY_WEIGHT = 0.15;
    private static final double ZONE_PLAYMAKING_OFF_SPEED_WEIGHT = 0.10;

    private static final double ZONE_PLAYMAKING_DEF_SPEED_WEIGHT = 0.10;
    private static final double ZONE_PLAYMAKING_DEF_SIZE_WEIGHT = 0.15;
    private static final double ZONE_PLAYMAKING_DEF_EXTERIOR_WEIGHT = 0.30;
    private static final double ZONE_PLAYMAKING_DEF_ENDURANCE_WEIGHT = 0.10;
    private static final double ZONE_PLAYMAKING_DEF_IQ_WEIGHT = 0.20;
    private static final double ZONE_PLAYMAKING_DEF_STEAL_WEIGHT = 0.15;

    private static final double ZONE23_DEFENSE_DEF_EXTERIEUR_WEIGHT = 0.10;
    private static final double ZONE23_DEFENSE_DEF_POSTE_WEIGHT = 0.20;
    private static final double ZONE23_DEFENSE_PROTECTION_CERCLE_WEIGHT = 0.30;
    private static final double ZONE23_DEFENSE_TIMING_BLOCK_WEIGHT = 0.15;
    private static final double ZONE23_DEFENSE_STEAL_WEIGHT = 0.02;
    private static final double ZONE23_DEFENSE_SIZE_WEIGHT = 0.06;
    private static final double ZONE23_DEFENSE_IQ_DEF_WEIGHT = 0.12;
    private static final double ZONE23_DEFENSE_ENDURANCE_WEIGHT = 0.05;

    private static final double ZONE32_DEFENSE_DEF_EXTERIEUR_WEIGHT = 0.35;
    private static final double ZONE32_DEFENSE_SPEED_WEIGHT = 0.12;
    private static final double ZONE32_DEFENSE_STEAL_WEIGHT = 0.12;
    private static final double ZONE32_DEFENSE_ENDURANCE_WEIGHT = 0.08;
    private static final double ZONE32_DEFENSE_IQ_DEF_WEIGHT = 0.18;
    private static final double ZONE32_DEFENSE_SIZE_WEIGHT = 0.05;
    private static final double ZONE32_DEFENSE_DEF_POSTE_WEIGHT = 0.04;
    private static final double ZONE32_DEFENSE_PROTECTION_CERCLE_WEIGHT = 0.03;
    private static final double ZONE32_DEFENSE_TIMING_BLOCK_WEIGHT = 0.03;

    private static final double ZONE212_DEFENSE_DEF_EXTERIEUR_WEIGHT = 0.10;
    private static final double ZONE212_DEFENSE_SPEED_WEIGHT = 0.08;
    private static final double ZONE212_DEFENSE_IQ_DEF_WEIGHT = 0.30;
    private static final double ZONE212_DEFENSE_STEAL_WEIGHT = 0.07;
    private static final double ZONE212_DEFENSE_DEF_POSTE_WEIGHT = 0.18;
    private static final double ZONE212_DEFENSE_PROTECTION_CERCLE_WEIGHT = 0.15;
    private static final double ZONE212_DEFENSE_ENDURANCE_WEIGHT = 0.06;
    private static final double ZONE212_DEFENSE_SIZE_WEIGHT = 0.06;

    private static final double REBOUND_SCORE_SIZE_WEIGHT = 0.18;
    private static final double REBOUND_SCORE_WEIGHT_WEIGHT = 0.10;
    private static final double REBOUND_SCORE_AGGRESSIVENESS_WEIGHT = 0.10;
    private static final double REBOUND_SCORE_REBOUND_AGGRESSIVENESS_WEIGHT = 0.18;
    private static final double REBOUND_SCORE_TIMING_WEIGHT = 0.18;
    private static final double REBOUND_SCORE_PHYSIQUE_WEIGHT = 0.14;
    private static final double REBOUND_SCORE_IQ_WEIGHT = 0.06;
    private static final double REBOUND_SCORE_ENDURANCE_WEIGHT = 0.06;

    private static final double STEAL_SCORE_SPEED_WEIGHT = 0.20;
    private static final double STEAL_SCORE_DEF_EXT_WEIGHT = 0.25;
    private static final double STEAL_SCORE_STEAL_WEIGHT = 0.30;
    private static final double STEAL_SCORE_BBIQ_DEF_WEIGHT = 0.15;
    private static final double STEAL_SCORE_ENDURANCE_WEIGHT = 0.05;
    private static final double STEAL_SCORE_PHYSIQUE_WEIGHT = 0.05;

    private PlayerScoreCalculator() {
    }

    public static void recalculateScores(InGamePlayer inGamePlayer) {
        Player player = inGamePlayer.getPlayer();
        inGamePlayer.setThreePtScore(calculateThreePtScore(player));
        inGamePlayer.setThreePtDefenseScore(calculateThreePtDefenseScore(player));
        inGamePlayer.setTwoPtScore(calculateTwoPtScore(player));
        inGamePlayer.setTwoPtDefenseScore(calculateTwoPtDefenseScore(player));
        inGamePlayer.setDriveScore(calculateDriveScore(player));
        inGamePlayer.setDriveDefenseScore(calculateDriveDefenseScore(player));
        inGamePlayer.setManToManPlaymakingOffScore(calculateManToManPlaymakingOffScore(player));
        inGamePlayer.setManToManPlaymakingDefScore(calculateManToManPlaymakingDefScore(player));
        inGamePlayer.setZonePlaymakingOffScore(calculateZonePlaymakingOffScore(player));
        inGamePlayer.setZonePlaymakingDefScore(calculateZonePlaymakingDefScore(player));
        inGamePlayer.setZone23DefenseScore(calculateZone23DefenseScore(player));
        inGamePlayer.setZone32DefenseScore(calculateZone32DefenseScore(player));
        inGamePlayer.setZone212DefenseScore(calculateZone212DefenseScore(player));
        inGamePlayer.setReboundScore(calculateReboundScore(player));
        inGamePlayer.setStealScore(calculateStealScore(player));
    }

    public static double calculateThreePtScore(Player player) {
        return THREE_PT_SCORE_SPEED_WEIGHT * player.getSpeed()
                + THREE_PT_SCORE_SIZE_WEIGHT * player.getSize()
                + THREE_PT_SCORE_ENDURANCE_WEIGHT * player.getEndurance()
                + THREE_PT_SCORE_RATING_WEIGHT * player.getTir3Pts()
                + THREE_PT_SCORE_IQ_WEIGHT * player.getBasketballIqOff();
    }

    public static double calculateThreePtDefenseScore(Player player) {
        return THREE_PT_DEFENSE_SCORE_SPEED_WEIGHT * player.getSpeed()
                + THREE_PT_DEFENSE_SCORE_SIZE_WEIGHT * player.getSize()
                + THREE_PT_DEFENSE_SCORE_DEF_EXTERIEUR_WEIGHT * player.getDefExterieur()
                + THREE_PT_DEFENSE_SCORE_ENDURANCE_WEIGHT * player.getEndurance()
                + THREE_PT_DEFENSE_SCORE_IQ_WEIGHT * player.getBasketballIqDef();
    }

    public static double calculateTwoPtScore(Player player) {
        return TWO_PT_SCORE_SPEED_WEIGHT * player.getSpeed()
                + TWO_PT_SCORE_SIZE_WEIGHT * player.getSize()
                + TWO_PT_SCORE_ENDURANCE_WEIGHT * player.getEndurance()
                + TWO_PT_SCORE_FINISH_WEIGHT * player.getFinitionAuCercle()
                + TWO_PT_SCORE_RATING_WEIGHT * player.getTir2Pts()
                + TWO_PT_SCORE_IQ_WEIGHT * player.getBasketballIqOff();
    }

    public static double calculateTwoPtDefenseScore(Player player) {
        return TWO_PT_DEFENSE_SCORE_SPEED_WEIGHT * player.getSpeed()
                + TWO_PT_DEFENSE_SCORE_SIZE_WEIGHT * player.getSize()
                + TWO_PT_DEFENSE_SCORE_ENDURANCE_WEIGHT * player.getEndurance()
                + TWO_PT_DEFENSE_SCORE_IQ_WEIGHT * player.getBasketballIqDef()
                + TWO_PT_DEFENSE_SCORE_STEAL_WEIGHT * player.getSteal()
                + TWO_PT_DEFENSE_SCORE_DEF_POSTE_WEIGHT * player.getDefPoste();
    }

    public static double calculateDriveScore(Player player) {
        return DRIVE_SCORE_SPEED_WEIGHT * player.getSpeed()
                + DRIVE_SCORE_SIZE_WEIGHT * player.getSize()
                + DRIVE_SCORE_ENDURANCE_WEIGHT * player.getEndurance()
                + DRIVE_SCORE_BALLHANDLING_WEIGHT * player.getBallhandling()
                + DRIVE_SCORE_FINISH_WEIGHT * player.getFinitionAuCercle()
                + DRIVE_SCORE_FLOATER_WEIGHT * player.getFloater()
                + DRIVE_SCORE_IQ_WEIGHT * player.getBasketballIqOff();
    }

    public static double calculateDriveDefenseScore(Player player) {
        return DRIVE_DEFENSE_SCORE_SPEED_WEIGHT * player.getSpeed()
                + DRIVE_DEFENSE_SCORE_SIZE_WEIGHT * player.getSize()
                + DRIVE_DEFENSE_SCORE_DEF_EXTERIEUR_WEIGHT * player.getDefExterieur()
                + DRIVE_DEFENSE_SCORE_ENDURANCE_WEIGHT * player.getEndurance()
                + DRIVE_DEFENSE_SCORE_IQ_WEIGHT * player.getBasketballIqDef()
                + DRIVE_DEFENSE_SCORE_STEAL_WEIGHT * player.getSteal()
                + DRIVE_DEFENSE_SCORE_DEF_POSTE_WEIGHT * player.getDefPoste();
    }

    public static double calculateManToManPlaymakingOffScore(Player player) {
        return MAN_TO_MAN_PLAYMAKING_OFF_SPEED_WEIGHT * player.getSpeed()
                + MAN_TO_MAN_PLAYMAKING_OFF_SIZE_WEIGHT * player.getSize()
                + MAN_TO_MAN_PLAYMAKING_OFF_ENDURANCE_WEIGHT * player.getEndurance()
                + MAN_TO_MAN_PLAYMAKING_OFF_PASSING_WEIGHT * player.getPassingSkills()
                + MAN_TO_MAN_PLAYMAKING_OFF_IQ_WEIGHT * player.getBasketballIqOff()
                + MAN_TO_MAN_PLAYMAKING_OFF_HANDLING_WEIGHT * player.getBallhandling()
                + MAN_TO_MAN_PLAYMAKING_OFF_THREE_PT_WEIGHT * player.getTir3Pts()
                + MAN_TO_MAN_PLAYMAKING_OFF_TWO_PT_WEIGHT * player.getTir2Pts()
                + MAN_TO_MAN_PLAYMAKING_OFF_FINISH_WEIGHT * player.getFinitionAuCercle()
                + MAN_TO_MAN_PLAYMAKING_OFF_FLOATER_WEIGHT * player.getFloater();
    }

    public static double calculateManToManPlaymakingDefScore(Player player) {
        return MAN_TO_MAN_PLAYMAKING_DEF_SPEED_WEIGHT * player.getSpeed()
                + MAN_TO_MAN_PLAYMAKING_DEF_SIZE_WEIGHT * player.getSize()
                + MAN_TO_MAN_PLAYMAKING_DEF_EXTERIOR_WEIGHT * player.getDefExterieur()
                + MAN_TO_MAN_PLAYMAKING_DEF_ENDURANCE_WEIGHT * player.getEndurance()
                + MAN_TO_MAN_PLAYMAKING_DEF_IQ_WEIGHT * player.getBasketballIqDef()
                + MAN_TO_MAN_PLAYMAKING_DEF_STEAL_WEIGHT * player.getSteal();
    }

    public static double calculateZonePlaymakingOffScore(Player player) {
        return ZONE_PLAYMAKING_OFF_PASSING_WEIGHT * player.getPassingSkills()
                + ZONE_PLAYMAKING_OFF_IQ_WEIGHT * player.getBasketballIqOff()
                + ZONE_PLAYMAKING_OFF_BALLHANDLING_WEIGHT * player.getBallhandling()
                + ZONE_PLAYMAKING_OFF_COACHABILITY_WEIGHT * player.getCoachability()
                + ZONE_PLAYMAKING_OFF_SPEED_WEIGHT * player.getSpeed();
    }

    public static double calculateZonePlaymakingDefScore(Player player) {
        return ZONE_PLAYMAKING_DEF_SPEED_WEIGHT * player.getSpeed()
                + ZONE_PLAYMAKING_DEF_SIZE_WEIGHT * player.getSize()
                + ZONE_PLAYMAKING_DEF_EXTERIOR_WEIGHT * player.getDefExterieur()
                + ZONE_PLAYMAKING_DEF_ENDURANCE_WEIGHT * player.getEndurance()
                + ZONE_PLAYMAKING_DEF_IQ_WEIGHT * player.getBasketballIqDef()
                + ZONE_PLAYMAKING_DEF_STEAL_WEIGHT * player.getSteal();
    }

    public static double calculateZone23DefenseScore(Player player) {
        return ZONE23_DEFENSE_DEF_EXTERIEUR_WEIGHT * player.getDefExterieur()
                + ZONE23_DEFENSE_DEF_POSTE_WEIGHT * player.getDefPoste()
                + ZONE23_DEFENSE_PROTECTION_CERCLE_WEIGHT * player.getProtectionCercle()
                + ZONE23_DEFENSE_TIMING_BLOCK_WEIGHT * player.getTimingBlock()
                + ZONE23_DEFENSE_STEAL_WEIGHT * player.getSteal()
                + ZONE23_DEFENSE_SIZE_WEIGHT * player.getSize()
                + ZONE23_DEFENSE_IQ_DEF_WEIGHT * player.getBasketballIqDef()
                + ZONE23_DEFENSE_ENDURANCE_WEIGHT * player.getEndurance();
    }

    public static double calculateZone32DefenseScore(Player player) {
        return ZONE32_DEFENSE_DEF_EXTERIEUR_WEIGHT * player.getDefExterieur()
                + ZONE32_DEFENSE_SPEED_WEIGHT * player.getSpeed()
                + ZONE32_DEFENSE_STEAL_WEIGHT * player.getSteal()
                + ZONE32_DEFENSE_ENDURANCE_WEIGHT * player.getEndurance()
                + ZONE32_DEFENSE_IQ_DEF_WEIGHT * player.getBasketballIqDef()
                + ZONE32_DEFENSE_SIZE_WEIGHT * player.getSize()
                + ZONE32_DEFENSE_DEF_POSTE_WEIGHT * player.getDefPoste()
                + ZONE32_DEFENSE_PROTECTION_CERCLE_WEIGHT * player.getProtectionCercle()
                + ZONE32_DEFENSE_TIMING_BLOCK_WEIGHT * player.getTimingBlock();
    }

    public static double calculateZone212DefenseScore(Player player) {
        return ZONE212_DEFENSE_DEF_EXTERIEUR_WEIGHT * player.getDefExterieur()
                + ZONE212_DEFENSE_SPEED_WEIGHT * player.getSpeed()
                + ZONE212_DEFENSE_IQ_DEF_WEIGHT * player.getBasketballIqDef()
                + ZONE212_DEFENSE_STEAL_WEIGHT * player.getSteal()
                + ZONE212_DEFENSE_DEF_POSTE_WEIGHT * player.getDefPoste()
                + ZONE212_DEFENSE_PROTECTION_CERCLE_WEIGHT * player.getProtectionCercle()
                + ZONE212_DEFENSE_ENDURANCE_WEIGHT * player.getEndurance()
                + ZONE212_DEFENSE_SIZE_WEIGHT * player.getSize();
    }

    public static double calculateReboundScore(Player player) {
        return REBOUND_SCORE_SIZE_WEIGHT * player.getSize()
                + REBOUND_SCORE_WEIGHT_WEIGHT * player.getWeight()
                + REBOUND_SCORE_AGGRESSIVENESS_WEIGHT * player.getAgressivite()
                + REBOUND_SCORE_REBOUND_AGGRESSIVENESS_WEIGHT * player.getAgressiviteRebond()
                + REBOUND_SCORE_TIMING_WEIGHT * player.getTimingRebond()
                + REBOUND_SCORE_PHYSIQUE_WEIGHT * player.getPhysique()
                + REBOUND_SCORE_IQ_WEIGHT * player.getIq()
                + REBOUND_SCORE_ENDURANCE_WEIGHT * player.getEndurance();
    }

    public static double calculateStealScore(Player player) {
        return STEAL_SCORE_SPEED_WEIGHT * player.getSpeed()
                + STEAL_SCORE_DEF_EXT_WEIGHT * player.getDefExterieur()
                + STEAL_SCORE_STEAL_WEIGHT * player.getSteal()
                + STEAL_SCORE_BBIQ_DEF_WEIGHT * player.getBasketballIqDef()
                + STEAL_SCORE_ENDURANCE_WEIGHT * player.getEndurance()
                + STEAL_SCORE_PHYSIQUE_WEIGHT * player.getPhysique();
    }
}
