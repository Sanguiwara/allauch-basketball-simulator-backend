package com.sanguiwara.badges;

import com.sanguiwara.type.ShotType;

public record ShotContext(ShotType shotType, boolean assisted, double advantage) implements Context {


    public static ShotContext forShot(ShotType shotType, boolean assisted, double advantage) {
        return new ShotContext(shotType, assisted, advantage);
    }

    public static ShotContext empty() {
        return new ShotContext(null, false, 0.0);
    }
}
