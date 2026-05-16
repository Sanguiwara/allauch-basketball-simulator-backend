package com.sanguiwara.progression.manager;

import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class MatchRatingCalculatorTest {

    private final MatchRatingCalculator calculator = new MatchRatingCalculator();

    static Stream<RatingCase> boundedRatingCases() {
        return Stream.of(
                new RatingCase("near neutral starter gets a bounded rating", 20, 10, 5, 11, 2, 3, 1, 1, 2, 1, 2, 0),
                new RatingCase("efficient scorer with solid passing grades well", 20, 16, 7, 10, 2, 3, 1, 1, 3, 1, 2, 0),
                new RatingCase("poor inefficient game stays inside the lower bound", 20, 4, 2, 12, 1, 1, 0, 0, 1, 1, 2, 0),
                new RatingCase("no field goal attempts skips efficiency without leaving range", 20, 8, 0, 0, 2, 3, 1, 1, 2, 1, 2, 0),
                new RatingCase("default usage stays neutral in usage component", 20, 10, 5, 11, 2, 3, 1, 1, 2, 10, 10, 10),
                new RatingCase("short good stint stays bounded by the same formula", 10, 8, 4, 5, 1, 2, 1, 0, 2, 1, 2, 0),
                new RatingCase("outlier boxscore receives full credit without exceeding max", 20, 80, 30, 35, 25, 25, 20, 20, 40, 30, 30, 30)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("boundedRatingCases")
    void compute_returnsRatingInClosedRangeFromBoxscore(RatingCase ratingCase) {
        InGamePlayer player = inGamePlayerFor(ratingCase);

        double rating = calculator.compute(player);

        assertThat(rating)
                .as(ratingCase.label)
                .isBetween(0.0, 10.0);
    }

    @Test
    void compute_returnsZeroWhenMinutesAreZero() {
        RatingCase ratingCase = new RatingCase(
                "zero minutes returns zero",
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                10,
                10,
                10
        );

        double rating = calculator.compute(inGamePlayerFor(ratingCase));

        assertThat(rating).isEqualTo(0.0);
    }

    @Test
    void compute_returnsMinimumRatingWhenEveryComponentIsAtMinimum() {
        RatingCase ratingCase = new RatingCase(
                "minimum",
                20,
                0,
                0,
                1,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0
        );

        double rating = calculator.compute(inGamePlayerFor(ratingCase));

        assertThat(rating).isEqualTo(0.0);
    }

    @Test
    void compute_returnsNeutralRatingWhenEveryComponentIsNeutral() {
        RatingCase ratingCase = new RatingCase(
                "neutral",
                100,
                50,
                45,
                100,
                10,
                15,
                4,
                3,
                12,
                10,
                10,
                10
        );

        double rating = calculator.compute(inGamePlayerFor(ratingCase));

        assertThat(rating).isCloseTo(5.0, within(0.000001));
    }

    @Test
    void compute_returnsMaximumRatingWhenEveryComponentReachesExcellentThreshold() {
        RatingCase ratingCase = new RatingCase(
                "excellent",
                20,
                20,
                7,
                10,
                5,
                5,
                2,
                2,
                5,
                30,
                30,
                30
        );

        double rating = calculator.compute(inGamePlayerFor(ratingCase));

        assertThat(rating).isCloseTo(10.0, within(0.000001));
    }

    static Stream<Arguments> invalidRatingCases() {
        return Stream.of(
                Arguments.of("negative minutes", new RatingCase("negative", -1, 0, 0, 0, 0, 0, 0, 0, 0, 10, 10, 10)),
                Arguments.of("made field goals above attempts", new RatingCase("invalid fg", 20, 0, 2, 1, 0, 0, 0, 0, 0, 10, 10, 10)),
                Arguments.of("usage above range", new RatingCase("invalid usage", 20, 0, 0, 0, 0, 0, 0, 0, 0, 31, 10, 10))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidRatingCases")
    void compute_rejectsInvalidInputs(String label, RatingCase ratingCase) {
        InGamePlayer player = inGamePlayerFor(ratingCase);

        assertThatThrownBy(() -> calculator.compute(player))
                .as(label)
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static InGamePlayer inGamePlayerFor(RatingCase ratingCase) {
        Player player = Player.builder()
                .id(UUID.randomUUID())
                .name("Test Player")
                .birthDate(2000)
                .build();
        InGamePlayer inGamePlayer = new InGamePlayer(player, UUID.randomUUID());
        inGamePlayer.setMinutesPlayed(ratingCase.minutes);
        inGamePlayer.setPoints(ratingCase.points);
        inGamePlayer.setFgm(ratingCase.fgm);
        inGamePlayer.setFga(ratingCase.fga);
        inGamePlayer.setOffensiveRebounds(ratingCase.offensiveRebounds);
        inGamePlayer.setDefensiveRebounds(ratingCase.defensiveRebounds);
        inGamePlayer.setSteals(ratingCase.steals);
        inGamePlayer.setBlocks(ratingCase.blocks);
        inGamePlayer.setAssists(ratingCase.assists);
        inGamePlayer.setUsageShoot(ratingCase.usageShoot);
        inGamePlayer.setUsageDrive(ratingCase.usageDrive);
        inGamePlayer.setUsagePost(ratingCase.usagePost);
        return inGamePlayer;
    }

    private record RatingCase(
            String label,
            int minutes,
            int points,
            int fgm,
            int fga,
            int offensiveRebounds,
            int defensiveRebounds,
            int steals,
            int blocks,
            int assists,
            int usageShoot,
            int usageDrive,
            int usagePost
    ) {
        @Override
        public String toString() {
            return label;
        }
    }
}
