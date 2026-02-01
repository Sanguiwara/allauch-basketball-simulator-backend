package com.sanguiwara.factory;

import com.sanguiwara.baserecords.AgeCategory;
import com.sanguiwara.baserecords.Gender;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.Team;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class TeamFactory {

    public Team generateTeam(AgeCategory ageCategory, Gender gender, List<Player> players) {
        var team = new Team(null, ageCategory, gender, generateRandomFrenchBasketClubName());
        team.setPlayers(players);
        return team;
    }

    /**
     * Génère un nom de club de basket "à la française".
     * Peut produire des noms déjà existants (au sens génération).
     */
    public String generateRandomFrenchBasketClubName() {
        return generateRandomFrenchBasketClubName(null);
    }

    /**
     * Génère un nom et garantit l'unicité si un Set est fourni.
     * - Si usedNames != null : tente jusqu'à 50 fois, puis ajoute un suffixe court si nécessaire.
     */
    public String generateRandomFrenchBasketClubName(Set<String> usedNames) {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        for (int attempt = 0; attempt < 50; attempt++) {
            String name = buildName(rnd);

            if (usedNames == null) return name;

            // add() retourne false si déjà présent
            if (usedNames.add(name)) return name;
        }

        // Fallback ultra simple si collisions (rare) : suffixe court
        String fallback = buildName(rnd) + " " + shortTag(rnd);
        if (usedNames != null) usedNames.add(fallback);
        return fallback;
    }

    private String buildName(ThreadLocalRandom rnd) {
        // Acronymes fréquents en France
        String[] acronyms = {"AS", "ES", "US", "CS", "JS", "SC", "Stade", "Racing", "Olympique"};

        // Noms "poétiques"/historiques qu'on voit souvent
        String[] identities = {
                "Étoile", "Aurore", "Espérance", "Avenir", "Fraternelle", "Amicale",
                "Union", "Entente", "Sporting", "Athlétique", "Jeunesse"
        };

        // “Formes” (souvent après une identité)
        String[] forms = {
                "Sportive", "Sportif", "Club", "Cercle", "Association", "Union Sportive", "Entente Sportive",
                "Sporting Club", "Club Sportif", "Jeunesse Sportive"
        };

        // Villes (tu peux enrichir librement)
        String[] cities = {
                "Paris", "Lyon", "Marseille", "Toulouse", "Nice", "Nantes", "Strasbourg", "Montpellier",
                "Bordeaux", "Lille", "Rennes", "Reims", "Le Havre", "Saint-Étienne", "Toulon", "Grenoble",
                "Dijon", "Angers", "Nîmes", "Aix-en-Provence", "Brest", "Clermont-Ferrand", "Nancy", "Tours",
                "Rouen", "Perpignan", "Caen", "Orléans", "Metz", "Besançon", "Amiens", "Annecy", "Poitiers",
                "La Rochelle", "Mulhouse", "Avignon", "Bayonne", "Pau"
        };

        // Qualificatifs géographiques “très FR”
        String[] geo = {
                "Centre", "Nord", "Sud", "Est", "Ouest", "Métropole", "Agglo", "Rive Gauche", "Rive Droite",
                "Université", "Côte", "Port", "Ville", "Quartier"
        };

        // Tags régionaux fréquents (style "Lorraine", "Bretagne"...)
        String[] regions = {
                "Lorraine", "Bretagne", "Normandie", "Alsace", "Provence", "Occitanie",
                "Aquitaine", "Auvergne", "Savoie", "Picardie", "Vendée", "Gascogne"
        };

        // Suffix basket
        String[] basketSuffix = {
                "Basket", "Basket Club", "Basket-ball", "Basketball", "Hoops"
        };

        // Choix de pattern “réalistes”
        int pattern = rnd.nextInt(6);

        String city = pick(cities, rnd);
        String suffix = pick(basketSuffix, rnd);

        // 0..100
        boolean withGeo = rnd.nextInt(100) < 35;      // 35% un qualif "Métropole", etc.
        boolean withRegion = rnd.nextInt(100) < 22;   // 22% un qualif "Lorraine", etc.

        String geoPart = withGeo ? " " + pick(geo, rnd) : "";
        String regionPart = withRegion ? " " + pick(regions, rnd) : "";

        String name;
        switch (pattern) {
            case 0 -> // "AS Lyon Basket"
                    name = pick(acronyms, rnd) + " " + city + geoPart + regionPart + " " + suffix;
            case 1 -> // "Étoile Sportive Rouen Métropole Basket"
                    name = pick(identities, rnd) + " " + pick(forms, rnd) + " " + city + geoPart + regionPart + " " + suffix;
            case 2 -> // "Union Sportive Nancy Lorraine Basket Club"
                    name = pick(new String[]{"Union Sportive", "Entente Sportive", "Sporting Club", "Club Sportif"}, rnd)
                            + " " + city + geoPart + regionPart + " " + suffix;
            case 3 -> // "Racing Bordeaux Basket"
                    name = pick(new String[]{"Racing", "Olympique", "Stade"}, rnd) + " " + city + geoPart + regionPart + " " + suffix;
            case 4 -> {
                // "US Marseille" (parfois sans suffix basket)
                boolean addSuffix = rnd.nextInt(100) < 70;
                name = pick(acronyms, rnd) + " " + city + geoPart + regionPart + (addSuffix ? " " + suffix : "");
            }
            default -> // "Amicale Sportive Lille Basket"
                    name = pick(new String[]{"Amicale", "Fraternelle", "Avenir", "Espérance"}, rnd)
                            + " " + pick(new String[]{"Sportive", "Athlétique", "Sporting"}, rnd)
                            + " " + city + geoPart + regionPart + " " + suffix;
        }

        return tidy(name);
    }

    private static String pick(String[] arr, ThreadLocalRandom rnd) {
        return arr[rnd.nextInt(arr.length)];
    }

    private static String tidy(String s) {
        // supprime espaces multiples + trim
        return s.replaceAll("\\s{2,}", " ").trim();
    }

    private static String shortTag(ThreadLocalRandom rnd) {
        // petit tag unique lisible (ex: "A7Q3")
        char a = (char) ('A' + rnd.nextInt(26));
        char b = (char) ('A' + rnd.nextInt(26));
        int n1 = rnd.nextInt(10);
        int n2 = rnd.nextInt(10);
        return "" + a + n1 + b + n2;
    }
}
