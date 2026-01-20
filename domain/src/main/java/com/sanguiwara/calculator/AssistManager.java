package com.sanguiwara.calculator;

import com.sanguiwara.baserecords.InGamePlayer;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
public class AssistManager {
    private final Random random;




    public InGamePlayer getAssister(InGamePlayer shooter, List<InGamePlayer> potentialPassers, double assistedShotPercentage) {
        boolean assisted = random.nextDouble() < assistedShotPercentage;
        InGamePlayer assister = null;
        if (assisted) {
            InGamePlayer result = null;
            double total = 0.0;
            for (InGamePlayer p : potentialPassers) {
                if (p == null || p == shooter) continue;
                total += Math.max(0.0, p.getAssistWeight());
            }

            double r = random.nextDouble() * total;
            for (InGamePlayer p : potentialPassers) {
                if ( p == shooter) continue;
                r -=  p.getAssistWeight();
                if (r <= 0.0) {
                    result = p;
                    break;
                }
            }
            assister = result;
        }
        return assister;
    }


}
