package com.sanguiwara.baserecords;


import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class GamePlan {

    public GamePlan(UUID id, Team ownerTeam, Team opponentTeam) {
        this.id = id;
        this.ownerTeam = ownerTeam;
        this.opponentTeam = opponentTeam;
    }



    private  List<InGamePlayer> activePlayers;

    private final UUID id;

    private final Team ownerTeam;
    private final Team opponentTeam;

    private DefenseType defenseType = DefenseType.MAN_TO_MAN;


    private  Map<Player, Player> matchups = new HashMap<>();
    private  Map<Position, InGamePlayer> positions = new HashMap<>();

    private double threePointAttemptShare =  1.0/3.0;
    private double midRangeAttemptShare = 1.0/3.0;
    private double driveAttemptShare = 1.0/3.0;

    private int totalShotNumber = 75;
    private double blockScore;
    private double blockProbability;
    private double assistProbability;

    public void addPossessions(int steals){
        totalShotNumber = totalShotNumber + steals;
    }
    public void removePossessions(int steals){
        totalShotNumber = totalShotNumber - steals;
    }



    public void setThreePointAttemptShare(double share) {
        if (share < 0.0 || share > 1.0) {
            throw new IllegalArgumentException(
                    "threePointAttemptShare must be in [0.0, 1.0], got=" + share
            );
        }
        this.threePointAttemptShare = share;
    }
    public void setMidRangeAttemptShare(double share) {
        if (share < 0.0 || share > 1.0) {
            throw new IllegalArgumentException(
                    "midRangeAttemptShare must be in [0.0, 1.0], got=" + share
            );
        }
        this.midRangeAttemptShare = share;
    }
    public void setDriveAttemptShare(double share) {
        if (share < 0.0 || share > 1.0) {
            throw new IllegalArgumentException("driveAttemptShare must be in [0.0, 1.0], got=" + share);
        }
        this.driveAttemptShare = share;
    }

    public int getThreePointAttempts() {
        return Math.toIntExact(Math.round(totalShotNumber * threePointAttemptShare));
    }
    public int getMidRangeAttempts() {
        return Math.toIntExact(Math.round(totalShotNumber * midRangeAttemptShare));
    }
    public int getDriveAttempts() {
        return Math.toIntExact(Math.round(totalShotNumber * driveAttemptShare));
    }

}
