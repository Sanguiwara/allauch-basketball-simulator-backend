package service;

import baserecords.Player;

import java.util.Random;

public final class PlayerRandomFactory {

    private final Random rng;

    public PlayerRandomFactory(long seed) {
        this.rng = new Random(seed);
    }
    public PlayerRandomFactory(){
            this. rng = new Random();
     }

    private int r(int min, int max) {
        return rng.nextInt(max - min + 1) + min;
    }

    public Player random(long id, String name) {

        Player player = new Player(
                id,
                name,
                r(1985, 2006), // birthDate (année) si tu gardes int

                // Tirs / finition
                r(30, 95), // tir3Pts
                r(30, 95), // tir2Pts
                r(30, 95), // lancerFranc
                r(20, 90), // floater
                r(30, 95), // finitionAuCercle
                r(30, 95),
                r(30, 95), // speed
                r(30, 95), // ballhandling
                r(55, 95), // size
                r(30, 95), // weight

                // Défense / rebond
                r(30, 95), // defExterieur
                r(30, 95), // defPoste
                r(30, 95), // protectionCercle
                r(30, 95), // timingRebond
                r(30, 95), // agressiviteRebond
                r(30, 95), // steal


                // Physique / mental / skills
                r(30, 95), // physique
                r(30, 95), // basketballIqOff
                r(30, 95), // basketballIqDef
                r(30, 95), // passingSkills
                r(30, 95), // iq
                r(30, 95), // endurance

                r(30, 95), // solidite

                // Potentiel
                r(30, 95), // potentielSkill
                r(30, 95), // potentielPhysique

                // Attitude / comportement
                r(30, 95), // coachability
                r(0, 100),  // ego
                r(30, 95), // softSkills
                r(30, 95)  // leadership
        );
        return player;
    }
}