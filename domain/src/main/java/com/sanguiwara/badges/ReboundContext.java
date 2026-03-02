package com.sanguiwara.badges;

public record ReboundContext(ReboundType reboundType) implements Context {
    public static ReboundContext offensive() {
        return new ReboundContext(ReboundType.OFFENSIVE);
    }

    public static ReboundContext defensive() {
        return new ReboundContext(ReboundType.DEFENSIVE);
    }
}

