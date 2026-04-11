package com.sanguiwara.baserecords;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Matchup assignments for a defensive game plan.
 * Direction is explicit: defender -> attacker.
 */
public final class Matchups {

    private final Map<MatchupDefender, MatchupAttacker> defenderToAttacker = new HashMap<>();

    public static Matchups empty() {
        return new Matchups();
    }

    public static Matchups of(Map<MatchupDefender, MatchupAttacker> assignments) {
        Matchups matchups = new Matchups();
        if (assignments != null) {
            assignments.forEach(matchups::assign);
        }
        return matchups;
    }

    public void assign(MatchupDefender defender, MatchupAttacker attacker) {
        defenderToAttacker.put(
                Objects.requireNonNull(defender, "defender"),
                Objects.requireNonNull(attacker, "attacker")
        );
    }

    public Player attackerFor(Player defender) {
        if (defender == null) {
            return null;
        }
        MatchupAttacker attacker = defenderToAttacker.get(new MatchupDefender(defender));
        return attacker == null ? null : attacker.player();
    }

    public Player defenderFor(Player attacker) {
        if (attacker == null) {
            return null;
        }
        return defenderToAttacker.entrySet().stream()
                .filter(entry -> attacker.equals(entry.getValue().player()))
                .map(Map.Entry::getKey)
                .map(MatchupDefender::player)
                .findFirst()
                .orElse(null);
    }

    public Map<MatchupDefender, MatchupAttacker> asMap() {
        return Collections.unmodifiableMap(defenderToAttacker);
    }

    public Stream<Map.Entry<MatchupDefender, MatchupAttacker>> stream() {
        return defenderToAttacker.entrySet().stream();
    }

    public int size() {
        return defenderToAttacker.size();
    }

    public boolean isEmpty() {
        return defenderToAttacker.isEmpty();
    }
}
