package event;

public record ThreePointShotEvent(
        long shooterId,
        int shotIndex,
        boolean assisted,
        long assisterPlayerId,  // -1 si pas assisté
        double shotPct,
        boolean made
) {}
