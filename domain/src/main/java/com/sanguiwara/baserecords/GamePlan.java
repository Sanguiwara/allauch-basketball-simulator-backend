package com.sanguiwara.baserecords;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class GamePlan {


    private final UUID id;

    private final Team teamHome;
    private final Team teamVisitor;

    private  List<InGamePlayer> activePlayers;

    private  Map<Player, Player> matchups;
    private  Map<Position, InGamePlayer> positions;
    private int totalShotNumber;

    private double threePointAttemptShare; //[0;1]
    private double midRangeAttemptShare;
    private double driveAttemptShare;

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
