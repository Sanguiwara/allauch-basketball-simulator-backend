package com.sanguiwara.progression.manager;

import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class InactivityProgressionManager {


    private static final int MIN_SKILL_VALUE = 1;
    private static final int MAX_SKILL_VALUE = 99;

    private static final int MINUTES_OK_THRESHOLD = 10;
    private static final int MAX_INACTIVITY_SKILL_DECAY = 1;

    public void apply(InGamePlayer inGamePlayer) {
        int minutesPlayed = inGamePlayer.getMinutesPlayed();
        if (minutesPlayed >= MINUTES_OK_THRESHOLD) {
            return;
        }
        applyInactivityFactor(minutesPlayed, inGamePlayer.getPlayer());
    }

    private static void applyInactivityFactor(int minutesPlayed, Player player) {
        double inactivity = (MINUTES_OK_THRESHOLD - minutesPlayed) / (double) MINUTES_OK_THRESHOLD;
        // For any minutesPlayed < MINUTES_OK_THRESHOLD, apply a small deterministic decay (max 1 point).
        // Using ceil ensures minutes=9 still decays by -1, while minutes=10 yields 0.
        int decay = -(int) Math.ceil(MAX_INACTIVITY_SKILL_DECAY * inactivity);
        player.setTir3Pts(applyDelta(player.getTir3Pts(), decay));
        player.setTir2Pts(applyDelta(player.getTir2Pts(), decay));
        player.setFinitionAuCercle(applyDelta(player.getFinitionAuCercle(), decay));
        player.setTimingRebond(applyDelta(player.getTimingRebond(), decay));
        player.setAgressiviteRebond(applyDelta(player.getAgressiviteRebond(), decay));
        player.setSteal(applyDelta(player.getSteal(), decay));
        player.setTimingBlock(applyDelta(player.getTimingBlock(), decay));
        player.setProtectionCercle(applyDelta(player.getProtectionCercle(), decay));
    }

    private static int applyDelta(int currentSkill, int delta) {
        return Math.clamp(currentSkill + delta, MIN_SKILL_VALUE, MAX_SKILL_VALUE);
    }
}

