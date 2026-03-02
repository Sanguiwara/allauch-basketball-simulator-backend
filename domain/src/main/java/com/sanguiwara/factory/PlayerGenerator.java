package com.sanguiwara.factory;

import com.sanguiwara.baserecords.Player;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Random;

@RequiredArgsConstructor
public final class PlayerGenerator {

    private final Random rng;

    private int r(int min, int max) {
        return rng.nextInt(max - min + 1) + min;
    }

    public Player generatePlayer(String name) {
        String effectiveName = (name == null || name.isBlank()) ? generateRandomName() : name;

        // Player is now built via Lombok @Builder; keep the same random ranges and field intent.
        Player player = Player.builder()
                .id(null)
                .name(effectiveName)
                .birthDate(r(1985, 2006)) // birthDate (year) if you keep int
                .injured(false)

                // Shooting / finishing
                .tir3Pts(r(30, 95))
                .tir2Pts(r(30, 95))
                .lancerFranc(r(30, 95))
                .floater(r(20, 90))
                .finitionAuCercle(r(30, 95))
                .agressivite(r(30, 95))
                .speed(r(30, 95))
                .ballhandling(r(30, 95))
                .size(r(55, 95))
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
                .ego(r(0, 100))
                .softSkills(r(30, 95))
                .leadership(r(30, 95))
                .morale(r(30, 95))
                .build();

        // Avoid null collections when using Lombok builders (field initializers are overridden).
        player.setTeamsID(new HashSet<>());

        player.setBadgeIds(new HashSet<>());
//        player.getBadgeIds().add(0L);
//        player.getBadgeIds().add(1L);
//        player.getBadgeIds().add(2L);
//        player.getBadgeIds().add(3L);
//        player.getBadgeIds().add(4L);
//        player.getBadgeIds().add(5L);
//        player.getBadgeIds().add(6L);
//        player.getBadgeIds().add(7L);
//        player.getBadgeIds().add(8L);

        return player;
    }

    public String generateRandomName() {
        String[] firstNames = {
                "LeBron", "Kevin", "Stephen", "Luka", "Giannis",
                "Jayson", "Joel", "Nikola", "Damian", "Devin",
                "Anthony", "Kawhi", "Jimmy", "Donovan", "CJ"
        };

        String[] lastNames = {
                "James", "Durant", "Curry", "Doncic", "Antetokounmpo",
                "Tatum", "Embiid", "Jokic", "Lillard", "Booker",
                "Davis", "Leonard", "Butler", "Mitchell", "McCollum"
        };

        String firstName = firstNames[rng.nextInt(firstNames.length)];
        String lastName = lastNames[rng.nextInt(lastNames.length)];

        return firstName + " " + lastName;
    }
}
