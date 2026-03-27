package com.sanguiwara.progression.manager;

import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class MoraleProgressionManagerApplyMoraleFromPerformanceTest {

    private static final Logger log =
            LoggerFactory.getLogger(MoraleProgressionManagerApplyMoraleFromPerformanceTest.class);

    static Stream<Arguments> cases() {
        int[] egos = {0, 50, 99};
        int[] minutesPlayed = {5, 10, 15};

        return IntStream.of(egos)
                .boxed()
                .flatMap(ego ->
                        IntStream.of(minutesPlayed)
                                .boxed()
                                .flatMap(minutes ->
                                        IntStream.rangeClosed(1, 10)
                                                .mapToObj(rating -> Arguments.of(ego, minutes, rating))));
    }

    @ParameterizedTest(name = "ego={0}, minutes={1}, matchRating={2}")
    @MethodSource("cases")
    void applyMoraleFromPerformance_logsMoraleDelta_andMocksMatchRating(
            int ego,
            int minutesPlayed,
            int matchRating0to10,
            TestReporter reporter
    ) {
        MatchRatingCalculator matchRatingCalculator = Mockito.mock(MatchRatingCalculator.class);
        MoraleProgressionManager manager = new MoraleProgressionManager(matchRatingCalculator);

        Player player = Player.builder()
                .id(UUID.randomUUID())
                .name("Test Player")
                .birthDate(2000)
                .ego(ego)
                .morale(50)
                .build();

        InGamePlayer inGamePlayer = new InGamePlayer(player, UUID.randomUUID());
        inGamePlayer.setMinutesPlayed(minutesPlayed);

        when(matchRatingCalculator.compute(inGamePlayer)).thenReturn((double) matchRating0to10);

        int moraleBefore = player.getMorale();
        manager.applyMoraleFromPerformance(inGamePlayer);
        int moraleAfter = player.getMorale();
        int delta = moraleAfter - moraleBefore;

        reporter.publishEntry(
                "result",
                "ego=%d minutes=%d rating=%d moraleBefore=%d moraleAfter=%d delta=%d"
                        .formatted(ego, minutesPlayed, matchRating0to10, moraleBefore, moraleAfter, delta)
        );
        log.info(
                "applyMoraleFromPerformance: ego={}, minutes={}, rating={} -> morale {} -> {} (delta={})",
                ego, minutesPlayed, matchRating0to10, moraleBefore, moraleAfter, delta
        );

        Mockito.verify(matchRatingCalculator).compute(inGamePlayer);
        assertThat(inGamePlayer.getMatchRating()).isEqualTo(matchRating0to10);
        assertThat(moraleAfter).isBetween(1, 100);

        // Neutral performance should not change morale if playing time is acceptable.
        if (matchRating0to10 == 5 && minutesPlayed >= 10) {
            assertThat(delta).isZero();
        }

        // If you barely play, you get a morale penalty even with a neutral rating.
        if (matchRating0to10 == 5 && minutesPlayed == 5) {
            assertThat(delta).isNegative();
        }
    }
}
