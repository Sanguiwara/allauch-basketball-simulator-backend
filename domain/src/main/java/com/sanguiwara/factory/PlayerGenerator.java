package com.sanguiwara.factory;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.badges.AutoSkillBadges;
import com.sanguiwara.progression.archetype.PlayerArchetypeDefinition;
import com.sanguiwara.progression.archetype.PlayerArchetypes;
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
        Player.PlayerBuilder b = basePlayerBuilder(effectiveName)
                .archetype(PlayerArchetype.ALL_AROUND);

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
        PlayerArchetypeDefinition definition = PlayerArchetypes.definitionFor(effective);
        Player.PlayerBuilder builder = basePlayerBuilder(effectiveName)
                .archetype(definition.type());
        definition.applyInitialStats(builder, this::r);

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
