package com.sanguiwara.progression.archetype;

@FunctionalInterface
public interface StatRoller {

    int roll(int minInclusive, int maxInclusive);
}
