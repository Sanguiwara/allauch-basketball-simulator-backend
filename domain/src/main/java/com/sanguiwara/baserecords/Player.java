package com.sanguiwara.baserecords;


import com.sanguiwara.badges.AutoSkillBadges;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class Player {

    @Setter
    private Set<UUID> teamsID;
    @Setter
    private UUID clubID;
    @Setter
    private Set<Long> badgeIds;

    private final UUID id;
    private final String name;
    private final int birthDate;

    @Setter
    private boolean injured;

    // Tirs / finition
    private  int tir3Pts;
    private  int tir2Pts;
    @Setter
    private  int lancerFranc;
    @Setter
    private  int floater;

    private  int finitionAuCercle;
    @Setter
    private  int speed;
    @Setter
    private  int ballhandling;
    @Setter
    private  int size;
    @Setter
    private  int weight;
    @Setter
    private  int agressivite;

    // Défense / rebond
    private  int defExterieur;
    @Setter
    private  int defPoste;
    @Setter
    private  int protectionCercle;
    private  int timingRebond;
    @Setter
    private  int agressiviteRebond;
    private  int steal;
    private  int timingBlock;

    // Physique / mental / skills
    @Setter
    private  int physique;
    private  int basketballIqOff;
    @Setter
    private  int basketballIqDef;
    @Setter
    private  int passingSkills;
    @Setter
    private  int iq;
    @Setter
    private  int endurance;

    @Setter
    private  int solidite;

    // Potentiel
    @Setter
    private  int potentielSkill;
    @Setter
    private  int potentielPhysique;

    // Attitude / comportement
    @Setter
    private  int coachability;
    @Setter
    private  int ego;
    @Setter
    private  int softSkills;
    @Setter
    private  int leadership;
    @Setter
    private  int morale;

    // Custom setters (override Lombok-generated ones) to keep auto-badges in sync with stats.
    public void setTir3Pts(int tir3Pts) {
        this.tir3Pts = tir3Pts;
        AutoSkillBadges.sync(this, AutoSkillBadges.Skill.PRECISION_3PTS, tir3Pts);
    }

    public void setTir2Pts(int tir2Pts) {
        this.tir2Pts = tir2Pts;
        AutoSkillBadges.sync(this, AutoSkillBadges.Skill.PRECISION_2PTS, tir2Pts);
    }

    public void setFinitionAuCercle(int finitionAuCercle) {
        this.finitionAuCercle = finitionAuCercle;
        // Current mapping: Drive badge depends on finitionAuCercle.
        AutoSkillBadges.sync(this, AutoSkillBadges.Skill.DRIVE, finitionAuCercle);
    }

    public void setTimingRebond(int timingRebond) {
        this.timingRebond = timingRebond;
        // Current mapping: Rebond badge depends on timingRebond.
        AutoSkillBadges.sync(this, AutoSkillBadges.Skill.REBOND, timingRebond);
    }

    public void setSteal(int steal) {
        this.steal = steal;
        AutoSkillBadges.sync(this, AutoSkillBadges.Skill.INTERCEPTION, steal);
    }

    public void setTimingBlock(int timingBlock) {
        this.timingBlock = timingBlock;
        // Current mapping: Contre badge depends on timingBlock.
        AutoSkillBadges.sync(this, AutoSkillBadges.Skill.CONTRE, timingBlock);
    }

    public void setBasketballIqOff(int basketballIqOff) {
        this.basketballIqOff = basketballIqOff;
        AutoSkillBadges.sync(this, AutoSkillBadges.Skill.QI_BASKET_OFF, basketballIqOff);
    }

    public void setDefExterieur(int defExterieur) {
        this.defExterieur = defExterieur;
        AutoSkillBadges.sync(this, AutoSkillBadges.Skill.DEF_EXTER, defExterieur);
    }

    /**
     * Deep snapshot of this Player at the current instant (copies mutable collections).
     * Used to compute progression deltas after post-game mutations.
     */
    public Player snapshotPlayer() {
        Set<UUID> teamsCopy = (teamsID == null) ? new HashSet<>() : new HashSet<>(teamsID);
        Set<Long> badgeIdsCopy = (badgeIds == null) ? new HashSet<>() : new HashSet<>(badgeIds);

        return Player.builder()
                .teamsID(teamsCopy)
                .clubID(clubID)
                .badgeIds(badgeIdsCopy)
                .id(id)
                .name(name)
                .birthDate(birthDate)
                .injured(injured)
                .tir3Pts(tir3Pts)
                .tir2Pts(tir2Pts)
                .lancerFranc(lancerFranc)
                .floater(floater)
                .finitionAuCercle(finitionAuCercle)
                .speed(speed)
                .ballhandling(ballhandling)
                .size(size)
                .weight(weight)
                .agressivite(agressivite)
                .defExterieur(defExterieur)
                .defPoste(defPoste)
                .protectionCercle(protectionCercle)
                .timingRebond(timingRebond)
                .agressiviteRebond(agressiviteRebond)
                .steal(steal)
                .timingBlock(timingBlock)
                .physique(physique)
                .basketballIqOff(basketballIqOff)
                .basketballIqDef(basketballIqDef)
                .passingSkills(passingSkills)
                .iq(iq)
                .endurance(endurance)
                .solidite(solidite)
                .potentielSkill(potentielSkill)
                .potentielPhysique(potentielPhysique)
                .coachability(coachability)
                .ego(ego)
                .softSkills(softSkills)
                .leadership(leadership)
                .morale(morale)
                .build();
    }



    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player other)) return false;
        return Objects.equals(id, other.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
