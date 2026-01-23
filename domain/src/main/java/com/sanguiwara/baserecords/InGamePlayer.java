package com.sanguiwara.baserecords;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class InGamePlayer {

    private final Player player;

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

    private final int usageShoot;
    private final int usageDrive;
    private final int usagePost;

    private int minutesPlayed = 20;



    private boolean starter;




    // -------------------------
    // Boxscore helpers
    // -------------------------
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


    public void addOffensiveRebound(){
        this.offensiveRebounds++;
    }

    public void recordThreePointShot(boolean made) {
        this.fga++;
        this.threePointAttempt++;
        if (made) {
            this.fgm++;
            this.threePointMade++;
            this.points += 3;
        }
    }

    public void recordTwoPointShot(boolean made) {
        this.fga++;
        this.twoPointAttempts++;
        if (made) {
            this.fgm++;
            this.twoPointMade++;
            this.points += 2;
        }
    }

    public void recordDrive(boolean made) {
        this.fga++;
        this.driveAttempts++;
        if (made) {
            this.fgm++;
            this.driveMade++;
            this.points += 2;
        }
    }
}
