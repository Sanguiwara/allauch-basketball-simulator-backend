package event;

public record TwoPointShotEvent(
        long shooterId,
        int shotNumber,
        boolean assisted,
        Long assisterId,
        double shotPct,
        boolean made,
        double advantage2pt
) {}