package com.sanguiwara.progression;

import com.sanguiwara.baserecords.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Deltas only, aligned with the SQL table {@code player_progressions} for numeric attributes.
 * Null means "no change" for that attribute.
 * Badges are handled as set deltas (added/removed) and are not persisted in {@code player_progressions}.
 * When needed, the "badges earned during an event" snapshot is stored separately (see {@code player_progression_badges}).
 */
public record PlayerProgressionDelta(
        Integer tir3Pts,
        Integer tir2Pts,
        Integer lancerFranc,
        Integer floater,
        Integer finitionAuCercle,
        Integer speed,
        Integer ballhandling,
        Integer size,
        Integer weight,
        Integer agressivite,
        Integer defExterieur,
        Integer defPoste,
        Integer protectionCercle,
        Integer timingRebond,
        Integer agressiviteRebond,
        Integer steal,
        Integer timingBlock,
        Integer physique,
        Integer basketballIqOff,
        Integer basketballIqDef,
        Integer passingSkills,
        Integer iq,
        Integer endurance,
        Integer solidite,
        Integer potentielSkill,
        Integer potentielPhysique,
        Integer coachability,
        Integer ego,
        Integer softSkills,
        Integer leadership,
        Integer morale,
        Set<Long> badgesAdded,
        Set<Long> badgesRemoved
) {
    public static PlayerProgressionDelta between(Player before, Player after) {
        Set<Long> beforeBadges = before.getBadgeIds() == null ? Set.of() : before.getBadgeIds();
        Set<Long> afterBadges = after.getBadgeIds() == null ? Set.of() : after.getBadgeIds();
        Set<Long> badgesAdded = diff(afterBadges, beforeBadges);
        Set<Long> badgesRemoved = diff(beforeBadges, afterBadges);

        return new PlayerProgressionDelta(
                deltaOrNull(after.getTir3Pts() - before.getTir3Pts()),
                deltaOrNull(after.getTir2Pts() - before.getTir2Pts()),
                deltaOrNull(after.getLancerFranc() - before.getLancerFranc()),
                deltaOrNull(after.getFloater() - before.getFloater()),
                deltaOrNull(after.getFinitionAuCercle() - before.getFinitionAuCercle()),
                deltaOrNull(after.getSpeed() - before.getSpeed()),
                deltaOrNull(after.getBallhandling() - before.getBallhandling()),
                deltaOrNull(after.getSize() - before.getSize()),
                deltaOrNull(after.getWeight() - before.getWeight()),
                deltaOrNull(after.getAgressivite() - before.getAgressivite()),
                deltaOrNull(after.getDefExterieur() - before.getDefExterieur()),
                deltaOrNull(after.getDefPoste() - before.getDefPoste()),
                deltaOrNull(after.getProtectionCercle() - before.getProtectionCercle()),
                deltaOrNull(after.getTimingRebond() - before.getTimingRebond()),
                deltaOrNull(after.getAgressiviteRebond() - before.getAgressiviteRebond()),
                deltaOrNull(after.getSteal() - before.getSteal()),
                deltaOrNull(after.getTimingBlock() - before.getTimingBlock()),
                deltaOrNull(after.getPhysique() - before.getPhysique()),
                deltaOrNull(after.getBasketballIqOff() - before.getBasketballIqOff()),
                deltaOrNull(after.getBasketballIqDef() - before.getBasketballIqDef()),
                deltaOrNull(after.getPassingSkills() - before.getPassingSkills()),
                deltaOrNull(after.getIq() - before.getIq()),
                deltaOrNull(after.getEndurance() - before.getEndurance()),
                deltaOrNull(after.getSolidite() - before.getSolidite()),
                deltaOrNull(after.getPotentielSkill() - before.getPotentielSkill()),
                deltaOrNull(after.getPotentielPhysique() - before.getPotentielPhysique()),
                deltaOrNull(after.getCoachability() - before.getCoachability()),
                deltaOrNull(after.getEgo() - before.getEgo()),
                deltaOrNull(after.getSoftSkills() - before.getSoftSkills()),
                deltaOrNull(after.getLeadership() - before.getLeadership()),
                deltaOrNull(after.getMorale() - before.getMorale()),
                badgesAdded,
                badgesRemoved
        );
    }

    private static Integer deltaOrNull(int delta) {
        return delta == 0 ? null : delta;
    }

    private static Set<Long> diff(Set<Long> left, Set<Long> right) {
        Set<Long> diff = new HashSet<>(left);
        diff.removeAll(right);
        return Set.copyOf(diff);
    }
}
