package com.sanguiwara.factory;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.badges.AutoSkillBadges;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Random;

@RequiredArgsConstructor
public final class PlayerGenerator {

    private final Random rng;

    private int r(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min must be <= max (min=" + min + ", max=" + max + ")");
        }
        return rng.nextInt(max - min + 1) + min;
    }

    public Player generatePlayer() {
        return generateRandomPlayer();
    }


    /**
     * Player "lambda" like before: 30..95 everywhere (ego stays in [0..99]).
     */
    public Player generateRandomPlayer() {
        String effectiveName = generateRandomName();
        Player.PlayerBuilder b = basePlayerBuilder(effectiveName);

        // Shooting / finishing
        b.tir3Pts(r(30, 95))
                .tir2Pts(r(30, 95))
                .lancerFranc(r(30, 95))
                .floater(r(30, 95))
                .finitionAuCercle(r(30, 95))
                .agressivite(r(30, 95))
                .speed(r(30, 95))
                .ballhandling(r(30, 95))
                .size(r(30, 95))
                .weight(r(30, 95))

                // Defense / rebound
                .defExterieur(r(30, 95))
                .defPoste(r(30, 95))
                .protectionCercle(r(30, 95))
                .timingRebond(r(30, 95))
                .agressiviteRebond(r(30, 95))
                .steal(r(30, 95))
                .timingBlock(r(30, 95))

                // Physical / mental / skills
                .physique(r(30, 95))
                .basketballIqOff(r(30, 95))
                .basketballIqDef(r(30, 95))
                .passingSkills(r(30, 95))
                .iq(r(30, 95))
                .endurance(r(30, 95))
                .solidite(r(30, 95))

                // Potential
                .potentielSkill(r(30, 95))
                .potentielPhysique(r(30, 95))

                // Attitude / behavior
                .coachability(r(30, 95))
                .ego(r(0, 99))
                .softSkills(r(30, 95))
                .leadership(r(30, 95))
                .morale(r(30, 95));

        Player p = b.build();
        assignAutoBadges(p);
        return p;
    }


    public Player generatePlayer(PlayerArchetype archetype) {
        PlayerArchetype effective = (archetype == null)
                ? PlayerArchetype.defaultForGeneratePlayer(rng)
                : archetype;

        String effectiveName = generateRandomName();
        Player.PlayerBuilder builder = basePlayerBuilder(effectiveName);

        switch (effective) {
            case SOLDIER -> applySoldier(builder);
            case STRATEGIST -> applyStrategist(builder);
            case CROQUEUR -> applyCroqueur(builder);
            case WHITE_SHOOTER -> applyWhiteShooter(builder);
            case THREE_POINT_SHOOTER -> applyThreePointShooter(builder);
            case TWO_POINT_SCORER -> applyTwoPointScorer(builder);
            case DRIVE_SPECIALIST -> applyDriveSpecialist(builder);
            case YOUNG_STAR -> applyYoungStar(builder);
            case ALL_AROUND -> applyAllAround(builder);
            case ALL_STAR -> applyAllStar(builder);
        }

        Player p = builder.build();
        assignAutoBadges(p);
        return p;
    }



    public PlayerArchetype randomArchetype() {
        return PlayerArchetype.random(rng);
    }




    private Player.PlayerBuilder basePlayerBuilder(String effectiveName) {
        return Player.builder()
                .teamsID(new HashSet<>())
                .clubID(null)
                .badgeIds(new HashSet<>())
                .id(null)
                .name(effectiveName)
                .birthDate(r(1985, 2006))
                .injured(false);
    }

    private static void assignAutoBadges(Player player) {
        AutoSkillBadges.syncAll(player);
    }

    private void fillAll(Player.PlayerBuilder b, int min, int max) {
        // All stats are expected to stay in [0..99].
        b.tir3Pts(r(min, max))
                .tir2Pts(r(min, max))
                .lancerFranc(r(min, max))
                .floater(r(min, max))
                .finitionAuCercle(r(min, max))
                .agressivite(r(min, max))
                .speed(r(min, max))
                .ballhandling(r(min, max))
                .size(r(min, max))
                .weight(r(min, max))
                .defExterieur(r(min, max))
                .defPoste(r(min, max))
                .protectionCercle(r(min, max))
                .timingRebond(r(min, max))
                .agressiviteRebond(r(min, max))
                .steal(r(min, max))
                .timingBlock(r(min, max))
                .physique(r(min, max))
                .basketballIqOff(r(min, max))
                .basketballIqDef(r(min, max))
                .passingSkills(r(min, max))
                .iq(r(min, max))
                .endurance(r(min, max))
                .solidite(r(min, max))
                .potentielSkill(r(min, max))
                .potentielPhysique(r(min, max))
                .coachability(r(min, max))
                .ego(r(min, max))
                .softSkills(r(min, max))
                .leadership(r(min, max))
                .morale(r(min, max));
    }

    private void applySoldier(Player.PlayerBuilder b) {
        fillAll(b, 1, 78);

        b.physique(r(82, 99))
                .solidite(r(80, 99))
                .endurance(r(78, 96))
                .defExterieur(r(75, 95))
                .defPoste(r(75, 95))
                .protectionCercle(r(70, 92))
                .timingRebond(r(75, 95))
                .agressiviteRebond(r(78, 99))
                .steal(r(70, 92))
                .timingBlock(r(68, 92))
                .size(r(65, 92))
                .weight(r(65, 95))
                .coachability(r(70, 95))
                .ego(r(20, 60));

        // Not a priority
        b.tir3Pts(r(25, 65))
                .ballhandling(r(35, 70))
                .passingSkills(r(35, 72))
                .tir2Pts(r(35, 70));
    }

    private void applyStrategist(Player.PlayerBuilder b) {
        fillAll(b, 1, 82);

        b.basketballIqOff(r(82, 99))
                .basketballIqDef(r(75, 95))
                .iq(r(80, 99))
                .passingSkills(r(80, 99))
                .ballhandling(r(78, 97))
                .speed(r(60, 90))
                .coachability(r(72, 96))
                .ego(r(20, 55));

        b.physique(r(45, 75))
                .solidite(r(50, 78))
                .protectionCercle(r(35, 70))
                .timingBlock(r(35, 70));
    }

    private void applyCroqueur(Player.PlayerBuilder b) {
        fillAll(b, 1, 80);

        b.tir3Pts(r(75, 97))
                .tir2Pts(r(78, 99))
                .lancerFranc(r(75, 97))
                .floater(r(70, 95))
                .finitionAuCercle(r(72, 97))
                .agressivite(r(85, 99))
                .ballhandling(r(62, 92))
                .ego(99);

        // Less collective / defense not a priority
        b.passingSkills(r(1, 65))
                .basketballIqDef(r(1, 72))
                .defExterieur(r(1, 70))
                .defPoste(r(1, 70))
                .protectionCercle(r(1, 65))
                .timingBlock(r(1, 65))
                .coachability(r(1, 75));

    }

    private void applyWhiteShooter(Player.PlayerBuilder b) {
        fillAll(b, 1, 84);

        b.tir3Pts(r(78, 99))
                .tir2Pts(r(72, 94))
                .lancerFranc(r(78, 99))
                .floater(r(70, 92))
                .finitionAuCercle(r(70, 92))
                .basketballIqOff(r(70, 94))
                .iq(r(70, 94))
                .passingSkills(r(65, 90))
                .ballhandling(r(55, 85))
                .coachability(r(75, 97))
                .ego(r(15, 55));

        b.physique(r(1, 75))
                .protectionCercle(r(1, 70))
                .timingBlock(r(1, 70));
    }

    private void applyThreePointShooter(Player.PlayerBuilder b) {
        fillAll(b, 1, 82);

        b.tir3Pts(r(85, 99))
                .lancerFranc(r(78, 99))
                .tir2Pts(r(55, 82))
                .floater(r(45, 78))
                .finitionAuCercle(r(50, 82))
                .ballhandling(r(55, 85))
                .passingSkills(r(45, 78))
                .basketballIqOff(r(55, 85))
                .iq(r(50, 82))
                .ego(r(15, 65))
                .coachability(r(60, 92));
    }

    private void applyTwoPointScorer(Player.PlayerBuilder b) {
        fillAll(b, 1, 82);

        b.tir2Pts(r(85, 99))
                .floater(r(75, 97))
                .finitionAuCercle(r(78, 99))
                .lancerFranc(r(70, 95))
                .agressivite(r(70, 95))
                .size(r(55, 90))
                .weight(r(50, 90))
                .tir3Pts(r(30, 70))
                .ego(r(25, 75))
                .coachability(r(55, 90));
    }

    private void applyDriveSpecialist(Player.PlayerBuilder b) {
        fillAll(b, 1, 82);

        b.speed(r(82, 99))
                .ballhandling(r(75, 95))
                .finitionAuCercle(r(82, 99))
                .agressivite(r(82, 99))
                .lancerFranc(r(70, 95))
                .physique(r(60, 92))
                .endurance(r(65, 95))
                .tir3Pts(r(25, 70))
                .floater(r(60, 92))
                .ego(r(25, 75))
                .coachability(r(50, 88));
    }

    private void applyAllAround(Player.PlayerBuilder b) {
        fillAll(b, 60, 90);
        b.ego(r(20, 65));
    }

    private void applyAllStar(Player.PlayerBuilder b) {
        fillAll(b, 75, 99);
        b.ego(99)
                .coachability(r(1, 80));
    }

    private void applyYoungStar(Player.PlayerBuilder b) {
        fillAll(b, 20, 70);
        b.potentielSkill(99);
    }

    public String generateRandomName() {
        String[] firstNames = {
                // French
                "Lucas", "Enzo", "Mathis", "Hugo", "Theo", "Adrien", "Maxime", "Antoine", "Julien", "Nicolas",
                "Alexis", "Kevin", "Thomas", "Quentin", "Baptiste", "Romain", "Florian", "Benjamin", "Mehdi",
                // Maghrebi / Arab
                "Yacine", "Sofiane", "Ilyes", "Nabil", "Samir", "Walid", "Rayan", "Ismael", "Karim", "Amine",
                "Nassim", "Ayoub", "Kamel", "Mourad", "Anis", "Zinedine",
                // Subsaharan / African
                "Mamadou", "Ibrahim", "Moussa", "Abdoulaye", "Bakary", "Cheikh", "Ousmane", "Aliou", "Boubacar", "Seydou",
                // Mediterranean
                "Antonio", "Marco", "Matteo", "Sergio", "Jordi"
        };

        String[] lastNames = {
                // French
                "Morel", "Bernard", "Aubert", "Michel", "Rey", "Vidal", "Giraud", "Roux", "Blanc", "Masson",
                // Iberian / Mediterranean common in Marseille
                "Garcia", "Lopez", "Fernandez", "Martinez", "Sanchez", "Rossi", "Esposito", "Mattei",
                // Maghrebi / Arab
                "Benali", "Bensaid", "Ait Ahmed", "Khelifi", "Bouziane", "Boudjema", "Amrani", "Benkacem", "Brahimi",
                // Subsaharan / African
                "Diop", "Fofana", "Traore", "Keita", "Diallo", "Sow", "Ba", "Cisse", "Kone", "Camara"
        };

        String firstName = firstNames[rng.nextInt(firstNames.length)];
        String lastName = lastNames[rng.nextInt(lastNames.length)];

        return firstName + " " + lastName;
    }
}
