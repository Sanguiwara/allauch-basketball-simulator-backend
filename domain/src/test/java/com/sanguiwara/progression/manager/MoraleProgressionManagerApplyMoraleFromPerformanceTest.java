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
        int[] morales = {20, 40, 60, 80};
        int[] egos = {0, 50, 99};
        int[] minutesPlayed = {5, 10, 14, 15, 20};

        return IntStream.of(morales)
                .boxed()
                .flatMap(morale ->
                        IntStream.of(egos)
                                .boxed()
                                .flatMap(ego ->
                                        IntStream.of(minutesPlayed)
                                                .boxed()
                                                .flatMap(minutes ->
                                                        IntStream.rangeClosed(1, 10)
                                                                .mapToObj(rating -> Arguments.of(morale, ego, minutes, rating)))));
    }

    private static final String EXPECTED_MORALE_PROGRESSION_CSV = """
            20,0,0,1.0,14
            20,0,0,5.0,16
            20,0,0,10.0,19
            20,0,5,1.0,15
            20,0,5,5.0,17
            20,0,5,10.0,20
            20,0,9,1.0,17
            20,0,9,5.0,19
            20,0,9,10.0,23
            20,0,10,1.0,17
            20,0,10,5.0,19
            20,0,10,10.0,23
            20,0,14,1.0,17
            20,0,14,5.0,19
            20,0,14,10.0,24
            20,0,15,1.0,18
            20,0,15,5.0,20
            20,0,15,10.0,25
            20,0,20,1.0,18
            20,0,20,5.0,20
            20,0,20,10.0,25
            20,50,0,1.0,9
            20,50,0,5.0,12
            20,50,0,10.0,17
            20,50,5,1.0,12
            20,50,5,5.0,15
            20,50,5,10.0,20
            20,50,9,1.0,14
            20,50,9,5.0,17
            20,50,9,10.0,24
            20,50,10,1.0,14
            20,50,10,5.0,17
            20,50,10,10.0,24
            20,50,14,1.0,16
            20,50,14,5.0,19
            20,50,14,10.0,28
            20,50,15,1.0,17
            20,50,15,5.0,20
            20,50,15,10.0,29
            20,50,20,1.0,17
            20,50,20,5.0,20
            20,50,20,10.0,29
            20,99,0,1.0,3
            20,99,0,5.0,9
            20,99,0,10.0,16
            20,99,5,1.0,7
            20,99,5,5.0,12
            20,99,5,10.0,19
            20,99,9,1.0,10
            20,99,9,5.0,16
            20,99,9,10.0,25
            20,99,10,1.0,11
            20,99,10,5.0,17
            20,99,10,10.0,27
            20,99,14,1.0,14
            20,99,14,5.0,19
            20,99,14,10.0,32
            20,99,15,1.0,14
            20,99,15,5.0,20
            20,99,15,10.0,33
            20,99,20,1.0,14
            20,99,20,5.0,20
            20,99,20,10.0,33
            40,0,0,1.0,32
            40,0,0,5.0,35
            40,0,0,10.0,38
            40,0,5,1.0,34
            40,0,5,5.0,36
            40,0,5,10.0,40
            40,0,9,1.0,36
            40,0,9,5.0,38
            40,0,9,10.0,42
            40,0,10,1.0,36
            40,0,10,5.0,38
            40,0,10,10.0,42
            40,0,14,1.0,36
            40,0,14,5.0,39
            40,0,14,10.0,43
            40,0,15,1.0,37
            40,0,15,5.0,40
            40,0,15,10.0,44
            40,0,20,1.0,37
            40,0,20,5.0,40
            40,0,20,10.0,44
            40,50,0,1.0,26
            40,50,0,5.0,30
            40,50,0,10.0,36
            40,50,5,1.0,29
            40,50,5,5.0,34
            40,50,5,10.0,40
            40,50,9,1.0,32
            40,50,9,5.0,36
            40,50,9,10.0,43
            40,50,10,1.0,32
            40,50,10,5.0,36
            40,50,10,10.0,43
            40,50,14,1.0,35
            40,50,14,5.0,39
            40,50,14,10.0,47
            40,50,15,1.0,36
            40,50,15,5.0,40
            40,50,15,10.0,48
            40,50,20,1.0,36
            40,50,20,5.0,40
            40,50,20,10.0,48
            40,99,0,1.0,19
            40,99,0,5.0,26
            40,99,0,10.0,35
            40,99,5,1.0,23
            40,99,5,5.0,30
            40,99,5,10.0,39
            40,99,9,1.0,27
            40,99,9,5.0,35
            40,99,9,10.0,44
            40,99,10,1.0,28
            40,99,10,5.0,36
            40,99,10,10.0,46
            40,99,14,1.0,32
            40,99,14,5.0,39
            40,99,14,10.0,50
            40,99,15,1.0,33
            40,99,15,5.0,40
            40,99,15,10.0,51
            40,99,20,1.0,33
            40,99,20,5.0,40
            40,99,20,10.0,51
            60,0,0,1.0,50
            60,0,0,5.0,53
            60,0,0,10.0,58
            60,0,5,1.0,52
            60,0,5,5.0,56
            60,0,5,10.0,60
            60,0,9,1.0,55
            60,0,9,5.0,58
            60,0,9,10.0,62
            60,0,10,1.0,55
            60,0,10,5.0,58
            60,0,10,10.0,62
            60,0,14,1.0,56
            60,0,14,5.0,59
            60,0,14,10.0,63
            60,0,15,1.0,57
            60,0,15,5.0,60
            60,0,15,10.0,64
            60,0,20,1.0,57
            60,0,20,5.0,60
            60,0,20,10.0,64
            60,50,0,1.0,42
            60,50,0,5.0,48
            60,50,0,10.0,56
            60,50,5,1.0,47
            60,50,5,5.0,52
            60,50,5,10.0,60
            60,50,9,1.0,50
            60,50,9,5.0,56
            60,50,9,10.0,63
            60,50,10,1.0,50
            60,50,10,5.0,56
            60,50,10,10.0,63
            60,50,14,1.0,53
            60,50,14,5.0,59
            60,50,14,10.0,65
            60,50,15,1.0,55
            60,50,15,5.0,60
            60,50,15,10.0,66
            60,50,20,1.0,55
            60,50,20,5.0,60
            60,50,20,10.0,66
            60,99,0,1.0,34
            60,99,0,5.0,42
            60,99,0,10.0,53
            60,99,5,1.0,39
            60,99,5,5.0,48
            60,99,5,10.0,59
            60,99,9,1.0,45
            60,99,9,5.0,53
            60,99,9,10.0,64
            60,99,10,1.0,46
            60,99,10,5.0,55
            60,99,10,10.0,65
            60,99,14,1.0,50
            60,99,14,5.0,59
            60,99,14,10.0,68
            60,99,15,1.0,51
            60,99,15,5.0,60
            60,99,15,10.0,69
            60,99,20,1.0,51
            60,99,20,5.0,60
            60,99,20,10.0,69
            80,0,0,1.0,68
            80,0,0,5.0,72
            80,0,0,10.0,77
            80,0,5,1.0,71
            80,0,5,5.0,75
            80,0,5,10.0,80
            80,0,9,1.0,74
            80,0,9,5.0,77
            80,0,9,10.0,81
            80,0,10,1.0,74
            80,0,10,5.0,77
            80,0,10,10.0,81
            80,0,14,1.0,75
            80,0,14,5.0,79
            80,0,14,10.0,82
            80,0,15,1.0,76
            80,0,15,5.0,80
            80,0,15,10.0,83
            80,0,20,1.0,76
            80,0,20,5.0,80
            80,0,20,10.0,83
            80,50,0,1.0,59
            80,50,0,5.0,66
            80,50,0,10.0,75
            80,50,5,1.0,64
            80,50,5,5.0,71
            80,50,5,10.0,80
            80,50,9,1.0,68
            80,50,9,5.0,75
            80,50,9,10.0,82
            80,50,10,1.0,68
            80,50,10,5.0,75
            80,50,10,10.0,82
            80,50,14,1.0,72
            80,50,14,5.0,79
            80,50,14,10.0,84
            80,50,15,1.0,74
            80,50,15,5.0,80
            80,50,15,10.0,85
            80,50,20,1.0,74
            80,50,20,5.0,80
            80,50,20,10.0,85
            80,99,0,1.0,49
            80,99,0,5.0,59
            80,99,0,10.0,72
            80,99,5,1.0,55
            80,99,5,5.0,66
            80,99,5,10.0,79
            80,99,9,1.0,62
            80,99,9,5.0,72
            80,99,9,10.0,83
            80,99,10,1.0,63
            80,99,10,5.0,74
            80,99,10,10.0,84
            80,99,14,1.0,68
            80,99,14,5.0,79
            80,99,14,10.0,86
            80,99,15,1.0,70
            80,99,15,5.0,80
            80,99,15,10.0,87
            80,99,20,1.0,70
            80,99,20,5.0,80
            80,99,20,10.0,87
            """;

    static Stream<Arguments> expectedMoraleProgressionCases() {
        return EXPECTED_MORALE_PROGRESSION_CSV.lines()
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .map(line -> line.split(","))
                .map(parts -> Arguments.of(
                        Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]),
                        Double.parseDouble(parts[3]),
                        Integer.parseInt(parts[4])
                ));
    }

    @ParameterizedTest(name = "morale={0}, ego={1}, minutes={2}, rating={3} -> expected={4}")
    @MethodSource("expectedMoraleProgressionCases")
    void applyMoraleFromPerformance_appliesExpectedMoraleProgression(
            int moraleBefore,
            int ego,
            int minutesPlayed,
            double matchRating,
            int expectedMorale
    ) {
        MatchRatingCalculator matchRatingCalculator = Mockito.mock(MatchRatingCalculator.class);
        MoraleProgressionManager manager = new MoraleProgressionManager(matchRatingCalculator);
        Player player = playerWithMoraleAndEgo(moraleBefore, ego);
        InGamePlayer inGamePlayer = new InGamePlayer(player, UUID.randomUUID());
        inGamePlayer.setMinutesPlayed(minutesPlayed);

        when(matchRatingCalculator.compute(inGamePlayer)).thenReturn(matchRating);

        manager.applyMoraleFromPerformance(inGamePlayer);

        Mockito.verify(matchRatingCalculator).compute(inGamePlayer);
        assertThat(inGamePlayer.getMatchRating()).isEqualTo(matchRating);
        assertThat(player.getMorale())
                .as("morale=%s, ego=%s, minutes=%s, rating=%s".formatted(
                        moraleBefore, ego, minutesPlayed, matchRating
                ))
                .isEqualTo(expectedMorale);
    }

    @ParameterizedTest(name = "morale={0}, ego={1}, minutes={2}, matchRating={3}")
    @MethodSource("cases")
    void applyMoraleFromPerformance_logsMoraleDelta_andMocksMatchRating(
            int morale,
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
                .morale(morale)
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
                "morale=%d ego=%d minutes=%d rating=%d moraleAfter=%d delta=%d"
                        .formatted(moraleBefore, ego, minutesPlayed, matchRating0to10, moraleAfter, delta)
        );
        log.info(
                "applyMoraleFromPerformance: morale={}, ego={}, minutes={}, rating={} -> {} (delta={})",
                moraleBefore, ego, minutesPlayed, matchRating0to10, moraleAfter, delta
        );

        Mockito.verify(matchRatingCalculator).compute(inGamePlayer);
        assertThat(inGamePlayer.getMatchRating()).isEqualTo(matchRating0to10);
        assertThat(moraleAfter).isBetween(1, 100);

        // Neutral performance should not change morale if playing time is acceptable.
        if (matchRating0to10 == 5 && minutesPlayed >= 15) {
            assertThat(delta).isZero();
        }

        // If you play below the acceptable threshold, you get a morale penalty even with a neutral rating.
        if (matchRating0to10 == 5 && minutesPlayed < 15) {
            assertThat(delta).isNegative();
        }
    }

    private static Player playerWithMoraleAndEgo(int morale, int ego) {
        return Player.builder()
                .id(UUID.randomUUID())
                .name("Test Player")
                .birthDate(2000)
                .ego(ego)
                .morale(morale)
                .build();
    }
}
