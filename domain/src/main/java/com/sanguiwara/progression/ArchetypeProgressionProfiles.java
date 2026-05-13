package com.sanguiwara.progression;

import com.sanguiwara.badges.BadgeType;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.factory.PlayerArchetype;

import java.util.Map;
import java.util.Objects;

import static com.sanguiwara.progression.ProgressionSkillGroup.ATHLETIC;
import static com.sanguiwara.progression.ProgressionSkillGroup.BLOCK;
import static com.sanguiwara.progression.ProgressionSkillGroup.DRIVE;
import static com.sanguiwara.progression.ProgressionSkillGroup.FLOATER;
import static com.sanguiwara.progression.ProgressionSkillGroup.FREE_THROW;
import static com.sanguiwara.progression.ProgressionSkillGroup.INTERIOR_DEFENSE;
import static com.sanguiwara.progression.ProgressionSkillGroup.MENTAL;
import static com.sanguiwara.progression.ProgressionSkillGroup.PERIMETER_DEFENSE;
import static com.sanguiwara.progression.ProgressionSkillGroup.PLAYMAKING;
import static com.sanguiwara.progression.ProgressionSkillGroup.REBOUND;
import static com.sanguiwara.progression.ProgressionSkillGroup.RIM_PROTECTION;
import static com.sanguiwara.progression.ProgressionSkillGroup.STEAL;
import static com.sanguiwara.progression.ProgressionSkillGroup.THREE_POINT;
import static com.sanguiwara.progression.ProgressionSkillGroup.TWO_POINT;

public final class ArchetypeProgressionProfiles {

    private static final Map<PlayerArchetype, ArchetypeProgressionProfile> PROFILES = Map.ofEntries(
            Map.entry(PlayerArchetype.SOLDIER, soldier()),
            Map.entry(PlayerArchetype.STRATEGIST, strategist()),
            Map.entry(PlayerArchetype.CROQUEUR, croqueur()),
            Map.entry(PlayerArchetype.WHITE_SHOOTER, whiteShooter()),
            Map.entry(PlayerArchetype.THREE_POINT_SHOOTER, threePointShooter()),
            Map.entry(PlayerArchetype.TWO_POINT_SCORER, twoPointScorer()),
            Map.entry(PlayerArchetype.DRIVE_SPECIALIST, driveSpecialist()),
            Map.entry(PlayerArchetype.YOUNG_STAR, youngStar()),
            Map.entry(PlayerArchetype.ALL_AROUND, allAround()),
            Map.entry(PlayerArchetype.ALL_STAR, allStar())
    );

    private ArchetypeProgressionProfiles() {
    }

    public static ArchetypeProgressionProfile forArchetype(PlayerArchetype archetype) {
        Objects.requireNonNull(archetype, "archetype");
        return PROFILES.get(archetype);
    }

    private static ArchetypeProgressionProfile soldier() {
        return new ArchetypeProgressionProfile(
                PlayerArchetype.SOLDIER,
                Map.of(
                        TrainingType.DEFENSE, 1.25,
                        TrainingType.PHYSICAL, 1.20,
                        TrainingType.SHOOTING, 0.85,
                        TrainingType.PLAYMAKING, 0.95
                ),
                Map.ofEntries(
                        Map.entry(PERIMETER_DEFENSE, 1.15),
                        Map.entry(INTERIOR_DEFENSE, 1.15),
                        Map.entry(RIM_PROTECTION, 1.20),
                        Map.entry(REBOUND, 1.25),
                        Map.entry(STEAL, 1.20),
                        Map.entry(BLOCK, 1.25),
                        Map.entry(ATHLETIC, 1.15),
                        Map.entry(THREE_POINT, 0.80),
                        Map.entry(TWO_POINT, 0.90),
                        Map.entry(DRIVE, 0.95)
                ),
                Map.ofEntries(
                        Map.entry(REBOUND, 1.25),
                        Map.entry(STEAL, 1.25),
                        Map.entry(BLOCK, 1.25),
                        Map.entry(RIM_PROTECTION, 1.20),
                        Map.entry(PERIMETER_DEFENSE, 1.15),
                        Map.entry(INTERIOR_DEFENSE, 1.15),
                        Map.entry(THREE_POINT, 0.80),
                        Map.entry(TWO_POINT, 0.90),
                        Map.entry(DRIVE, 0.95)
                ),
                Map.ofEntries(
                        Map.entry(BadgeType.REBOUND, 1.35),
                        Map.entry(BadgeType.STEAL, 1.30),
                        Map.entry(BadgeType.BLOCK, 1.30),
                        Map.entry(BadgeType.DEF_EXTER, 1.15),
                        Map.entry(BadgeType.THREE_POINT, 0.80),
                        Map.entry(BadgeType.TWO_POINT, 0.90),
                        Map.entry(BadgeType.DRIVE, 0.95)
                )
        );
    }

    private static ArchetypeProgressionProfile strategist() {
        return new ArchetypeProgressionProfile(
                PlayerArchetype.STRATEGIST,
                Map.of(
                        TrainingType.PLAYMAKING, 1.25,
                        TrainingType.TACTICAL, 1.25,
                        TrainingType.SHOOTING, 1.05,
                        TrainingType.DEFENSE, 1.05
                ),
                Map.of(
                        PLAYMAKING, 1.25,
                        MENTAL, 1.20,
                        THREE_POINT, 1.05,
                        TWO_POINT, 1.05,
                        DRIVE, 1.05
                ),
                Map.of(
                        PLAYMAKING, 1.25,
                        MENTAL, 1.20,
                        THREE_POINT, 1.05,
                        TWO_POINT, 1.05,
                        DRIVE, 1.05
                ),
                Map.of(
                        BadgeType.ASSIST, 1.40,
                        BadgeType.THREE_POINT, 1.05,
                        BadgeType.TWO_POINT, 1.05,
                        BadgeType.DRIVE, 1.05,
                        BadgeType.STEAL, 1.05,
                        BadgeType.DEF_EXTER, 1.05
                )
        );
    }

    private static ArchetypeProgressionProfile croqueur() {
        return new ArchetypeProgressionProfile(
                PlayerArchetype.CROQUEUR,
                Map.of(
                        TrainingType.SHOOTING, 1.15,
                        TrainingType.DEFENSE, 0.80,
                        TrainingType.TACTICAL, 0.90,
                        TrainingType.PLAYMAKING, 0.90
                ),
                Map.of(
                        THREE_POINT, 1.15,
                        TWO_POINT, 1.20,
                        DRIVE, 1.15,
                        FLOATER, 1.10,
                        FREE_THROW, 1.10,
                        PLAYMAKING, 0.85,
                        PERIMETER_DEFENSE, 0.85,
                        INTERIOR_DEFENSE, 0.85
                ),
                Map.of(
                        THREE_POINT, 1.20,
                        TWO_POINT, 1.25,
                        DRIVE, 1.20,
                        PLAYMAKING, 0.85,
                        PERIMETER_DEFENSE, 0.85,
                        INTERIOR_DEFENSE, 0.85
                ),
                Map.of(
                        BadgeType.THREE_POINT, 1.20,
                        BadgeType.TWO_POINT, 1.30,
                        BadgeType.DRIVE, 1.20,
                        BadgeType.ASSIST, 0.85,
                        BadgeType.REBOUND, 0.90,
                        BadgeType.STEAL, 0.85,
                        BadgeType.BLOCK, 0.85,
                        BadgeType.DEF_EXTER, 0.85
                )
        );
    }

    private static ArchetypeProgressionProfile whiteShooter() {
        return new ArchetypeProgressionProfile(
                PlayerArchetype.WHITE_SHOOTER,
                Map.of(
                        TrainingType.SHOOTING, 1.20,
                        TrainingType.PLAYMAKING, 1.05,
                        TrainingType.TACTICAL, 1.05,
                        TrainingType.PHYSICAL, 0.90
                ),
                Map.of(
                        THREE_POINT, 1.30,
                        FREE_THROW, 1.20,
                        TWO_POINT, 1.05,
                        PLAYMAKING, 1.05,
                        MENTAL, 1.05,
                        DRIVE, 0.90,
                        RIM_PROTECTION, 0.85,
                        BLOCK, 0.85
                ),
                Map.of(
                        THREE_POINT, 1.30,
                        TWO_POINT, 1.05,
                        PLAYMAKING, 1.05,
                        DRIVE, 0.90,
                        BLOCK, 0.85
                ),
                Map.of(
                        BadgeType.THREE_POINT, 1.35,
                        BadgeType.TWO_POINT, 1.05,
                        BadgeType.ASSIST, 1.05,
                        BadgeType.DRIVE, 0.90,
                        BadgeType.REBOUND, 0.90,
                        BadgeType.BLOCK, 0.85
                )
        );
    }

    private static ArchetypeProgressionProfile threePointShooter() {
        return new ArchetypeProgressionProfile(
                PlayerArchetype.THREE_POINT_SHOOTER,
                Map.of(
                        TrainingType.SHOOTING, 1.25,
                        TrainingType.PHYSICAL, 1.05,
                        TrainingType.DEFENSE, 0.90
                ),
                Map.of(
                        THREE_POINT, 1.45,
                        FREE_THROW, 1.10,
                        TWO_POINT, 0.90,
                        DRIVE, 0.75,
                        FLOATER, 0.80
                ),
                Map.of(
                        THREE_POINT, 1.40,
                        TWO_POINT, 0.85,
                        DRIVE, 0.80
                ),
                Map.of(
                        BadgeType.THREE_POINT, 1.50,
                        BadgeType.TWO_POINT, 0.85,
                        BadgeType.DRIVE, 0.80
                )
        );
    }

    private static ArchetypeProgressionProfile twoPointScorer() {
        return new ArchetypeProgressionProfile(
                PlayerArchetype.TWO_POINT_SCORER,
                Map.of(
                        TrainingType.SHOOTING, 1.25,
                        TrainingType.PHYSICAL, 1.05,
                        TrainingType.DEFENSE, 0.90
                ),
                Map.of(
                        TWO_POINT, 1.45,
                        DRIVE, 1.10,
                        FLOATER, 1.05,
                        THREE_POINT, 0.75,
                        FREE_THROW, 1.05
                ),
                Map.of(
                        TWO_POINT, 1.40,
                        DRIVE, 1.05,
                        THREE_POINT, 0.80
                ),
                Map.of(
                        BadgeType.TWO_POINT, 1.50,
                        BadgeType.DRIVE, 1.05,
                        BadgeType.THREE_POINT, 0.80
                )
        );
    }

    private static ArchetypeProgressionProfile driveSpecialist() {
        return new ArchetypeProgressionProfile(
                PlayerArchetype.DRIVE_SPECIALIST,
                Map.of(
                        TrainingType.SHOOTING, 1.15,
                        TrainingType.PHYSICAL, 1.10,
                        TrainingType.PLAYMAKING, 1.10,
                        TrainingType.DEFENSE, 0.90
                ),
                Map.of(
                        DRIVE, 1.45,
                        FLOATER, 1.30,
                        ATHLETIC, 1.10,
                        PLAYMAKING, 1.10,
                        TWO_POINT, 1.05,
                        THREE_POINT, 0.75
                ),
                Map.of(
                        DRIVE, 1.45,
                        TWO_POINT, 1.05,
                        THREE_POINT, 0.75
                ),
                Map.of(
                        BadgeType.DRIVE, 1.50,
                        BadgeType.TWO_POINT, 1.05,
                        BadgeType.THREE_POINT, 0.75,
                        BadgeType.ASSIST, 1.10
                )
        );
    }

    private static ArchetypeProgressionProfile youngStar() {
        return new ArchetypeProgressionProfile(
                PlayerArchetype.YOUNG_STAR,
                Map.of(
                        TrainingType.SHOOTING, 1.15,
                        TrainingType.DEFENSE, 1.15,
                        TrainingType.PHYSICAL, 1.15,
                        TrainingType.PLAYMAKING, 1.15,
                        TrainingType.TACTICAL, 1.15
                ),
                Map.of(),
                Map.of(
                        THREE_POINT, 1.15,
                        TWO_POINT, 1.15,
                        DRIVE, 1.15,
                        REBOUND, 1.15,
                        STEAL, 1.15,
                        BLOCK, 1.15,
                        RIM_PROTECTION, 1.15
                ),
                Map.of(
                        BadgeType.THREE_POINT, 1.15,
                        BadgeType.TWO_POINT, 1.15,
                        BadgeType.DRIVE, 1.15,
                        BadgeType.REBOUND, 1.15,
                        BadgeType.STEAL, 1.15,
                        BadgeType.ASSIST, 1.15,
                        BadgeType.BLOCK, 1.15,
                        BadgeType.DEF_EXTER, 1.15
                )
        );
    }

    private static ArchetypeProgressionProfile allAround() {
        return new ArchetypeProgressionProfile(PlayerArchetype.ALL_AROUND, Map.of(), Map.of(), Map.of(), Map.of());
    }

    private static ArchetypeProgressionProfile allStar() {
        return new ArchetypeProgressionProfile(
                PlayerArchetype.ALL_STAR,
                Map.of(
                        TrainingType.SHOOTING, 0.95,
                        TrainingType.DEFENSE, 0.95,
                        TrainingType.PHYSICAL, 0.95,
                        TrainingType.PLAYMAKING, 0.95,
                        TrainingType.TACTICAL, 0.95
                ),
                Map.of(),
                Map.of(
                        THREE_POINT, 0.95,
                        TWO_POINT, 0.95,
                        DRIVE, 0.95,
                        REBOUND, 0.95,
                        STEAL, 0.95,
                        BLOCK, 0.95,
                        RIM_PROTECTION, 0.95
                ),
                Map.of(
                        BadgeType.THREE_POINT, 0.95,
                        BadgeType.TWO_POINT, 0.95,
                        BadgeType.DRIVE, 0.95,
                        BadgeType.REBOUND, 0.95,
                        BadgeType.STEAL, 0.95,
                        BadgeType.ASSIST, 0.95,
                        BadgeType.BLOCK, 0.95,
                        BadgeType.DEF_EXTER, 0.95
                )
        );
    }
}
