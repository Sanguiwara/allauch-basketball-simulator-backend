package com.sanguiwara.badges;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;


public final class BadgeCatalog {
    static final long THREE_POINT_SPECIALIST_ID = 0L;
    static final long TWO_POINT_SPECIALIST_ID = 1L;
    static final long DRIVE_FINISHER_ID = 2L;
    static final long REBOUND_HUNTER_ID = 3L;
    static final long THIEF_ID = 4L;
    static final long PLAYMAKER_ID = 5L;
    static final long DEF_REBOUND_SPECIALIST_ID = 6L;
    static final long ASSISTED_SHOT_BOOST_ID = 7L;
    static final long CRAZY_SHOOTER_ID = 8L;

    private static final double DEFAULT_DROP_RATE = 0.3;


    public static Map<Long, Badge> badgeMap () {
        return Map.of(
                // Stable IDs for persistence (start at 0)
                THREE_POINT_SPECIALIST_ID, threePointSpecialist(),
                TWO_POINT_SPECIALIST_ID, twoPointSpecialist(),
                DRIVE_FINISHER_ID, driveFinisher(),
                REBOUND_HUNTER_ID, reboundHunter(),
                THIEF_ID, thief(),
                PLAYMAKER_ID, playmaker(),
                DEF_REBOUND_SPECIALIST_ID, defensiveReboundSpecialist(),
                ASSISTED_SHOT_BOOST_ID, assistedShotBoost(),
                CRAZY_SHOOTER_ID, crazyShooter()
        );
    }


    private static Badge threePointSpecialist() {
        EnumMap<BadgeType, List<Modifier>> mods = new EnumMap<>(BadgeType.class);
        mods.put(BadgeType.THREE_POINT, List.of(new Modifier(Target.SHOT_PCT, ModifierOp.ADD, 0.03)));
        return new StandardBadge(THREE_POINT_SPECIALIST_ID, "Three Point Specialist", DEFAULT_DROP_RATE, EnumSet.of(BadgeType.THREE_POINT), mods);
    }

    private static Badge twoPointSpecialist() {
        EnumMap<BadgeType, List<Modifier>> mods = new EnumMap<>(BadgeType.class);
        mods.put(BadgeType.TWO_POINT, List.of(new Modifier(Target.SHOT_PCT, ModifierOp.ADD, 0.02)));
        return new StandardBadge(TWO_POINT_SPECIALIST_ID, "Two Point Specialist", DEFAULT_DROP_RATE, EnumSet.of(BadgeType.TWO_POINT), mods);
    }

    private static Badge driveFinisher() {
        EnumMap<BadgeType, List<Modifier>> mods = new EnumMap<>(BadgeType.class);
        mods.put(BadgeType.DRIVE, List.of(new Modifier(Target.SHOT_PCT, ModifierOp.ADD, 0.02)));
        return new StandardBadge(DRIVE_FINISHER_ID, "Drive Finisher", DEFAULT_DROP_RATE, EnumSet.of(BadgeType.DRIVE), mods);
    }

    private static Badge reboundHunter() {
        EnumMap<BadgeType, List<Modifier>> mods = new EnumMap<>(BadgeType.class);
        mods.put(BadgeType.REBOUND, List.of(new Modifier(Target.REBOUND_SCORE, ModifierOp.MUL, 1.10)));
        return new StandardBadge(REBOUND_HUNTER_ID, "Rebound Hunter", DEFAULT_DROP_RATE, EnumSet.of(BadgeType.REBOUND), mods);
    }

    private static Badge thief() {
        EnumMap<BadgeType, List<Modifier>> mods = new EnumMap<>(BadgeType.class);
        mods.put(BadgeType.STEAL, List.of(new Modifier(Target.STEAL_SCORE, ModifierOp.MUL, 1.10)));
        return new StandardBadge(THIEF_ID, "Thief", DEFAULT_DROP_RATE, EnumSet.of(BadgeType.STEAL), mods);
    }

    private static Badge playmaker() {
        EnumMap<BadgeType, List<Modifier>> mods = new EnumMap<>(BadgeType.class);
        mods.put(BadgeType.ASSIST, List.of(new Modifier(Target.PLAYMAKING_CONTRIBUTION, ModifierOp.MUL, 1.10)));
        return new StandardBadge(PLAYMAKER_ID, "Playmaker", DEFAULT_DROP_RATE, EnumSet.of(BadgeType.ASSIST), mods);
    }

    private static Badge defensiveReboundSpecialist() {
        return new Badge() {
            @Override public long id() { return DEF_REBOUND_SPECIALIST_ID; }
            @Override public String name() { return "Defensive Rebound Specialist"; }
            @Override public double dropRate() { return DEFAULT_DROP_RATE; }
            @Override public java.util.Set<BadgeType> types() { return EnumSet.of(BadgeType.REBOUND); }

            @Override
            public List<Modifier> modifiersFor(BadgeType type, Context context) {
                if (type != BadgeType.REBOUND) return List.of();
                if (!(context instanceof ReboundContext(ReboundType reboundType))) return List.of();
                if (reboundType != ReboundType.DEFENSIVE) return List.of();
                return List.of(new Modifier(Target.REBOUND_SCORE, ModifierOp.MUL, 1.20));
            }
        };
    }

    private static Badge assistedShotBoost() {
        return new Badge() {
            @Override public long id() { return ASSISTED_SHOT_BOOST_ID; }
            @Override public String name() { return "Assisted Shot Boost"; }
            @Override public double dropRate() { return DEFAULT_DROP_RATE; }
            @Override public java.util.Set<BadgeType> types() { return EnumSet.of(BadgeType.THREE_POINT, BadgeType.TWO_POINT, BadgeType.DRIVE); }

            @Override
            public List<Modifier> modifiersFor(BadgeType type, Context context) {
                if (!(context instanceof ShotContext sc)) return List.of();
                if (!sc.assisted()) return List.of();
                return switch (type) {
                    case THREE_POINT, TWO_POINT, DRIVE -> List.of(new Modifier(Target.SHOT_PCT, ModifierOp.ADD, 0.02));
                    default -> List.of();
                };
            }
        };
    }

    private static Badge crazyShooter(){
        EnumMap<BadgeType, List<Modifier>> mods = new EnumMap<>(BadgeType.class);
        mods.put(BadgeType.DRIVE, List.of(new Modifier(Target.SHOT_PCT, ModifierOp.ADD, 0.02)));
        mods.put(BadgeType.THREE_POINT, List.of(new Modifier(Target.SHOT_PCT, ModifierOp.ADD, 0.02)));
        return new StandardBadge(CRAZY_SHOOTER_ID, "Crazy Shooter", DEFAULT_DROP_RATE, EnumSet.of(BadgeType.DRIVE, BadgeType.THREE_POINT), mods);

    }
}
