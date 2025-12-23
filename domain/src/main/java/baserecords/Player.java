package baserecords;



public record Player(
        Long id,
        String name,
        int birthDate,

        // Tirs / finition
        int tir3Pts,
        int tir2Pts,
        int lancerFranc,
        int floater,
        int finitionAuCercle,
        int speed,
        int ballhandling,
        int size,
        int weight,
        int agressivite,

        // Défense / rebond
        int defExterieur,
        int defPoste,
        int protectionCercle,
        int timingRebond,
        int agressiviteRebond,
        int steal,

        // Physique / mental / skills
        int physique,
        int basketballIqOff,
        int basketballIqDef,
        int passingSkills,
        int iq,
        int endurance,



        int solidite,

        // Potentiel
        int potentielSkill,
        int potentielPhysique,

        // Attitude / comportement
        int coachability,
        int ego,
        int softSkills,
        int leadership

) {




}
