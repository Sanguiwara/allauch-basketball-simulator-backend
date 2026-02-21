package com.sanguiwara.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "player_progressions")
public class PlayerProgressionEntity {

    @EmbeddedId
    private PlayerProgressionId id;

    @MapsId("playerId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private PlayerEntity player;

    @Column(name = "tir_3_pts")
    private Integer tir3Pts;

    @Column(name = "tir_2_pts")
    private Integer tir2Pts;

    @Column(name = "lancer_franc")
    private Integer lancerFranc;

    @Column(name = "floater")
    private Integer floater;

    @Column(name = "finition_au_cercle")
    private Integer finitionAuCercle;

    @Column(name = "speed")
    private Integer speed;

    @Column(name = "ballhandling")
    private Integer ballhandling;

    @Column(name = "size")
    private Integer size;

    @Column(name = "weight")
    private Integer weight;

    @Column(name = "agressivite")
    private Integer agressivite;

    @Column(name = "def_exterieur")
    private Integer defExterieur;

    @Column(name = "def_poste")
    private Integer defPoste;

    @Column(name = "protection_cercle")
    private Integer protectionCercle;

    @Column(name = "timing_rebond")
    private Integer timingRebond;

    @Column(name = "agressivite_rebond")
    private Integer agressiviteRebond;

    @Column(name = "steal")
    private Integer steal;

    @Column(name = "timing_block")
    private Integer timingBlock;

    @Column(name = "physique")
    private Integer physique;

    @Column(name = "basketball_iq_off")
    private Integer basketballIqOff;

    @Column(name = "basketball_iq_def")
    private Integer basketballIqDef;

    @Column(name = "passing_skills")
    private Integer passingSkills;

    @Column(name = "iq")
    private Integer iq;

    @Column(name = "endurance")
    private Integer endurance;

    @Column(name = "solidite")
    private Integer solidite;

    @Column(name = "potentiel_skill")
    private Integer potentielSkill;

    @Column(name = "potentiel_physique")
    private Integer potentielPhysique;

    @Column(name = "coachability")
    private Integer coachability;

    @Column(name = "ego")
    private Integer ego;

    @Column(name = "soft_skills")
    private Integer softSkills;

    @Column(name = "leadership")
    private Integer leadership;

    @Column(name = "morale")
    private Integer morale;
}

