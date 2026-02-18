package com.sanguiwara.progression;

import com.sanguiwara.baserecords.Player;

/**
 * Deltas only, aligned with the SQL table {@code player_progressions}.
 * Null means "no change" for that attribute.
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
        Integer morale
) {
    public static PlayerProgressionDelta between(Player before, Player after) {
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
                deltaOrNull(after.getMorale() - before.getMorale())
        );
    }

    private static Integer deltaOrNull(int delta) {
        return delta == 0 ? null : delta;
    }
}
