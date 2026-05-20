package com.sanguiwara.progression.training;

public enum TrainablePlayerStat {
    TIR_3_PTS("tir3Pts"),
    TIR_2_PTS("tir2Pts"),
    LANCER_FRANC("lancerFranc"),
    FLOATER("floater"),
    FINITION_AU_CERCLE("finitionAuCercle"),
    BALLHANDLING("ballhandling"),
    PASSING_SKILLS("passingSkills"),
    BASKETBALL_IQ_OFF("basketballIqOff"),
    DEF_EXTERIEUR("defExterieur"),
    DEF_POSTE("defPoste"),
    PROTECTION_CERCLE("protectionCercle"),
    STEAL("steal"),
    TIMING_BLOCK("timingBlock"),
    BASKETBALL_IQ_DEF("basketballIqDef"),
    PHYSIQUE("physique"),
    SPEED("speed"),
    ENDURANCE("endurance"),
    SOLIDITE("solidite"),
    IQ("iq"),
    MORALE("morale");

    private final String playerField;

    TrainablePlayerStat(String playerField) {
        this.playerField = playerField;
    }

    public String playerField() {
        return playerField;
    }
}
