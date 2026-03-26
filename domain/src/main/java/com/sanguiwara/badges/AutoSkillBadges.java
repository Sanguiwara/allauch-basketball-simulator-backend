package com.sanguiwara.badges;

import com.sanguiwara.baserecords.Player;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Auto-awarded badges based on player stats thresholds (no random drop).
 * Ranks:
 * - Fer:     >= 30
 * - Bronze:  >= 50
 * - Or:      >= 70
 * - Platine: >= 90
 * At most one rank badge per skill is present on a Player at any time.
 */
public final class AutoSkillBadges {

    private AutoSkillBadges() {}

    public static final int THRESHOLD_FER = 30;
    public static final int THRESHOLD_BRONZE = 50;
    public static final int THRESHOLD_OR = 70;
    public static final int THRESHOLD_PLATINE = 90;

    private static final double DROP_RATE = 0.0;

    // Keep stable IDs for persistence: existing badges are 0..8 in BadgeCatalog.
    public static final long PRECISION_3PTS_FER_ID = 9L;
    public static final long PRECISION_3PTS_BRONZE_ID = 10L;
    public static final long PRECISION_3PTS_OR_ID = 11L;
    public static final long PRECISION_3PTS_PLATINE_ID = 12L;

    public static final long PRECISION_2PTS_FER_ID = 13L;
    public static final long PRECISION_2PTS_BRONZE_ID = 14L;
    public static final long PRECISION_2PTS_OR_ID = 15L;
    public static final long PRECISION_2PTS_PLATINE_ID = 16L;

    public static final long DRIVE_FER_ID = 17L;
    public static final long DRIVE_BRONZE_ID = 18L;
    public static final long DRIVE_OR_ID = 19L;
    public static final long DRIVE_PLATINE_ID = 20L;

    public static final long REBOND_FER_ID = 21L;
    public static final long REBOND_BRONZE_ID = 22L;
    public static final long REBOND_OR_ID = 23L;
    public static final long REBOND_PLATINE_ID = 24L;

    public static final long INTERCEPTION_FER_ID = 25L;
    public static final long INTERCEPTION_BRONZE_ID = 26L;
    public static final long INTERCEPTION_OR_ID = 27L;
    public static final long INTERCEPTION_PLATINE_ID = 28L;

    public static final long CONTRE_FER_ID = 29L;
    public static final long CONTRE_BRONZE_ID = 30L;
    public static final long CONTRE_OR_ID = 31L;
    public static final long CONTRE_PLATINE_ID = 32L;

    public static final long QI_BASKET_OFF_FER_ID = 33L;
    public static final long QI_BASKET_OFF_BRONZE_ID = 34L;
    public static final long QI_BASKET_OFF_OR_ID = 35L;
    public static final long QI_BASKET_OFF_PLATINE_ID = 36L;

    public static final long DEF_EXTER_FER_ID = 37L;
    public static final long DEF_EXTER_BRONZE_ID = 38L;
    public static final long DEF_EXTER_OR_ID = 39L;
    public static final long DEF_EXTER_PLATINE_ID = 40L;

    public enum Skill {
        PRECISION_3PTS(PRECISION_3PTS_FER_ID, PRECISION_3PTS_BRONZE_ID, PRECISION_3PTS_OR_ID, PRECISION_3PTS_PLATINE_ID,
                "Precision 3pts", EnumSet.of(BadgeType.THREE_POINT)),
        PRECISION_2PTS(PRECISION_2PTS_FER_ID, PRECISION_2PTS_BRONZE_ID, PRECISION_2PTS_OR_ID, PRECISION_2PTS_PLATINE_ID,
                "Precision 2pts", EnumSet.of(BadgeType.TWO_POINT)),
        DRIVE(DRIVE_FER_ID, DRIVE_BRONZE_ID, DRIVE_OR_ID, DRIVE_PLATINE_ID,
                "Drive", EnumSet.of(BadgeType.DRIVE)),
        REBOND(REBOND_FER_ID, REBOND_BRONZE_ID, REBOND_OR_ID, REBOND_PLATINE_ID,
                "Rebond", EnumSet.of(BadgeType.REBOUND)),
        INTERCEPTION(INTERCEPTION_FER_ID, INTERCEPTION_BRONZE_ID, INTERCEPTION_OR_ID, INTERCEPTION_PLATINE_ID,
                "Interception", EnumSet.of(BadgeType.STEAL)),
        CONTRE(CONTRE_FER_ID, CONTRE_BRONZE_ID, CONTRE_OR_ID, CONTRE_PLATINE_ID,
                "Contre", EnumSet.of(BadgeType.BLOCK)),
        QI_BASKET_OFF(QI_BASKET_OFF_FER_ID, QI_BASKET_OFF_BRONZE_ID, QI_BASKET_OFF_OR_ID, QI_BASKET_OFF_PLATINE_ID,
                "QI Basket Off", EnumSet.of(BadgeType.ASSIST)),
        DEF_EXTER(DEF_EXTER_FER_ID, DEF_EXTER_BRONZE_ID, DEF_EXTER_OR_ID, DEF_EXTER_PLATINE_ID,
                "Def Exter", EnumSet.of(BadgeType.DEF_EXTER));

        private final long ferId;
        private final long bronzeId;
        private final long orId;
        private final long platineId;
        private final String baseName;
        private final Set<BadgeType> types;

        Skill(long ferId, long bronzeId, long orId, long platineId, String baseName, Set<BadgeType> types) {
            this.ferId = ferId;
            this.bronzeId = bronzeId;
            this.orId = orId;
            this.platineId = platineId;
            this.baseName = baseName;
            this.types = types;
        }

        public long ferId() { return ferId; }
        public long bronzeId() { return bronzeId; }
        public long orId() { return orId; }
        public long platineId() { return platineId; }
        public String baseName() { return baseName; }
        public Set<BadgeType> types() { return types; }
    }

    public enum Rank {
        FER("Fer"),
        BRONZE("Bronze"),
        OR("Or"),
        PLATINE("Platine");

        private final String label;
        Rank(String label) { this.label = label; }
        public String label() { return label; }
    }

    public static Map<Long, Badge> badgeMap() {
        Map<Long, Badge> m = new HashMap<>();
        for (Skill skill : Skill.values()) {
            m.put(skill.ferId(), rankedBadge(skill, Rank.FER, skill.ferId()));
            m.put(skill.bronzeId(), rankedBadge(skill, Rank.BRONZE, skill.bronzeId()));
            m.put(skill.orId(), rankedBadge(skill, Rank.OR, skill.orId()));
            m.put(skill.platineId(), rankedBadge(skill, Rank.PLATINE, skill.platineId()));
        }
        return Map.copyOf(m);
    }

    public static void syncAll(Player player) {
        if (player == null) return;

        sync(player, Skill.PRECISION_3PTS, player.getTir3Pts());
        sync(player, Skill.PRECISION_2PTS, player.getTir2Pts());

        // Current mapping (can be adjusted): Drive uses finitionAuCercle.
        sync(player, Skill.DRIVE, player.getFinitionAuCercle());

        // Current mapping (can be adjusted): Rebond uses timingRebond.
        sync(player, Skill.REBOND, player.getTimingRebond());

        sync(player, Skill.INTERCEPTION, player.getSteal());

        // Current mapping (can be adjusted): Contre uses timingBlock.
        sync(player, Skill.CONTRE, player.getTimingBlock());

        sync(player, Skill.QI_BASKET_OFF, player.getBasketballIqOff());
        sync(player, Skill.DEF_EXTER, player.getDefExterieur());
    }

    public static void sync(Player player, Skill skill, int statValue) {

        Set<Long> badgeIds = player.getBadgeIds();
        if (badgeIds == null) {
            badgeIds = new HashSet<>();
            player.setBadgeIds(badgeIds);
        } else if (!(badgeIds instanceof HashSet)) {
            // Some call sites use Set.of(...) which is immutable; enforce mutability.
            badgeIds = new HashSet<>(badgeIds);
            player.setBadgeIds(badgeIds);
        }

        // Remove every rank for this skill, then add the current one (if any).
        badgeIds.remove(skill.ferId());
        badgeIds.remove(skill.bronzeId());
        badgeIds.remove(skill.orId());
        badgeIds.remove(skill.platineId());

        Long chosen = badgeIdForStat(skill, statValue);
        if (chosen != null) {
            badgeIds.add(chosen);
        }
    }

    private static Long badgeIdForStat(Skill skill, int statValue) {
        if (statValue >= THRESHOLD_PLATINE) return skill.platineId();
        if (statValue >= THRESHOLD_OR) return skill.orId();
        if (statValue >= THRESHOLD_BRONZE) return skill.bronzeId();
        if (statValue >= THRESHOLD_FER) return skill.ferId();
        return null;
    }

    private static Badge rankedBadge(Skill skill, Rank rank, long id) {
        EnumMap<BadgeType, List<Modifier>> mods = modifiersFor(skill, rank);
        return new StandardBadge(
                id,
                skill.baseName() + " (" + rank.label() + ")",
                DROP_RATE,
                skill.types(),
                mods
        );
    }

    private static EnumMap<BadgeType, List<Modifier>> modifiersFor(Skill skill, Rank rank) {
        EnumMap<BadgeType, List<Modifier>> mods = new EnumMap<>(BadgeType.class);

        switch (skill) {
            case PRECISION_3PTS -> mods.put(BadgeType.THREE_POINT, List.of(new Modifier(Target.SHOT_PCT, ModifierOp.ADD, shotPctBonus(rank))));
            case PRECISION_2PTS -> mods.put(BadgeType.TWO_POINT, List.of(new Modifier(Target.SHOT_PCT, ModifierOp.ADD, shotPctBonus(rank))));
            case DRIVE -> mods.put(BadgeType.DRIVE, List.of(new Modifier(Target.SHOT_PCT, ModifierOp.ADD, shotPctBonus(rank))));
            case REBOND -> mods.put(BadgeType.REBOUND, List.of(new Modifier(Target.REBOUND_SCORE, ModifierOp.MUL, scoreMult(rank))));
            case INTERCEPTION -> mods.put(BadgeType.STEAL, List.of(new Modifier(Target.STEAL_SCORE, ModifierOp.MUL, scoreMult(rank))));
            case CONTRE -> mods.put(BadgeType.BLOCK, List.of(new Modifier(Target.BLOCK_SCORE, ModifierOp.MUL, scoreMult(rank))));
            case QI_BASKET_OFF -> mods.put(BadgeType.ASSIST, List.of(new Modifier(Target.PLAYMAKING_CONTRIBUTION, ModifierOp.MUL, playmakingMult(rank))));
            case DEF_EXTER -> mods.put(BadgeType.DEF_EXTER, List.of(new Modifier(Target.DEFENSE_SCORE, ModifierOp.MUL, defenseMult(rank))));
        }
        return mods;
    }

    private static double shotPctBonus(Rank rank) {
        return switch (rank) {
            case FER -> 0.010;
            case BRONZE -> 0.020;
            case OR -> 0.040;
            case PLATINE -> 0.060;
        };
    }

    private static double scoreMult(Rank rank) {
        return switch (rank) {
            case FER -> 1.03;
            case BRONZE -> 1.06;
            case OR -> 1.10;
            case PLATINE -> 1.15;
        };
    }

    private static double playmakingMult(Rank rank) {
        return switch (rank) {
            case FER -> 1.02;
            case BRONZE -> 1.04;
            case OR -> 1.06;
            case PLATINE -> 1.08;
        };
    }

    private static double defenseMult(Rank rank) {
        return switch (rank) {
            case FER -> 1.02;
            case BRONZE -> 1.04;
            case OR -> 1.06;
            case PLATINE -> 1.08;
        };
    }
}
