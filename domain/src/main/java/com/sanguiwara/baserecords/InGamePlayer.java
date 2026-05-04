package com.sanguiwara.baserecords;

import com.sanguiwara.calculator.PlayerScoreCalculator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class InGamePlayer {
    private UUID id;
    private final Player player;
    private final UUID gamePlanId;

    private int usageShoot = 10;
    private int usageDrive = 10 ;
    private int usagePost = 10;


    // --- Inputs calculés pour ce match ---
    private double playmakingContribution;
    private double reboundContribution;
    private double assistWeight;
    private double reboundWeight;

    private double threePointContribution;
    private double threePointWeight;

    private double twoPointContribution;
    private double twoPointWeight;

    private double driveContribution;
    private double driveWeight;

    private double blockContribution;
    private double blockWeight;
    private double stealContribution;
    private double stealWeight;


    // --- Outputs (boxscore) ---
    private int assists;
    private int points;
    private int offensiveRebounds;
    private int defensiveRebounds;
    private int steals;
    private int blocks;

    private int fga;   // Field Goals Attempted
    private int fgm;   // Field Goals Made

    private int threePointAttempt;   // 3PT Attempted
    private int threePointMade;   // 3PT Made

    private int twoPointAttempts; // 2PT Attempted (optionnel, pratique)
    private int twoPointMade; // 2PT Made

    private int driveAttempts;
    private int driveMade;



    private int minutesPlayed = 20;

    // Post-game computed rating in [0..10].
    private double matchRating;
    private double threePtScore;
    private double threePtDefenseScore;
    private double twoPtScore;
    private double twoPtDefenseScore;
    private double driveScore;
    private double driveDefenseScore;
    private double manToManPlaymakingOffScore;
    private double manToManPlaymakingDefScore;
    private double zonePlaymakingOffScore;
    private double zonePlaymakingDefScore;
    private double zone23DefenseScore;
    private double zone32DefenseScore;
    private double zone212DefenseScore;
    private double reboundScore;
    private double stealScore;




    private boolean starter;




    public void addAssist() {
        this.assists++;
    }
    public void addThreePointShot(){
        this.fga++;
        this.threePointAttempt++;
    }

    public void addTwoPointShot(){
        this.fga++;
        this.twoPointAttempts++;
    }

    public void addDrive(){
        this.fga++;
        this.driveAttempts++;
    }
    public void addBlock(){
        this.blocks++;
    }


    public void addOffensiveRebound(){
        this.offensiveRebounds++;
    }

    public void addDefensiveRebound() {
        this.defensiveRebounds++;
    }
    public void recordThreePointShot(boolean made) {
        if (made) {
            this.fgm++;
            this.threePointMade++;
            this.points += 3;
        }
    }

    public void recordTwoPointShot(boolean made) {

        if (made) {
            this.fgm++;
            this.twoPointMade++;
            this.points += 2;
        }
    }

    public void recordDrive(boolean made) {
        if (made) {
            this.fgm++;
            this.driveMade++;
            this.points += 2;
        }
    }

    public void recalculateScores() {
        PlayerScoreCalculator.recalculateScores(this);
    }
}
