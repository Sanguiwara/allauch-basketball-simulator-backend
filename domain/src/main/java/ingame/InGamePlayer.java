package ingame;

import baserecords.Player;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class InGamePlayer {

    private final Player player;

    // --- Inputs calculés pour ce match ---
    private double playmakingContribution;  // somme des adv vs matchups
    private double assistWeight;            // calculé après contributions

    // --- Outputs (boxscore) ---
    private int assists;
    private int points;

    private int fga;   // Field Goals Attempted
    private int fgm;   // Field Goals Made

    private int tpa;   // 3PT Attempted
    private int tpm;   // 3PT Made

    private int twoPa; // 2PT Attempted (optionnel, pratique)
    private int twoPm; // 2PT Made
    private final int usageShoot;
    private final int usageDrive;
    private final int usagePost;

    // -------------------------
    // Match inputs accumulation
    // -------------------------
    public void addPlaymakingContribution(double delta) {
        this.playmakingContribution += delta;
    }



    // -------------------------
    // Boxscore helpers
    // -------------------------
    public void addAssist() {
        this.assists++;
    }

    public void recordThreePointShot(boolean made) {
        this.fga++;
        this.tpa++;

        if (made) {
            this.fgm++;
            this.tpm++;
            this.points += 3;
        }
    }

    public void recordTwoPointShot(boolean made) {
        this.fga++;
        this.twoPa++;

        if (made) {
            this.fgm++;
            this.twoPm++;
            this.points += 2;
        }
    }
}
