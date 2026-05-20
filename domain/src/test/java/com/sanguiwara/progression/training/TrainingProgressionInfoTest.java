package com.sanguiwara.progression.training;

import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.modifiers.PlayerModifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingProgressionInfoTest {

    @Test
    void defaultsExposeProgressionMetadataForEachTrainingType() {
        var progressions = TrainingProgressions.defaults();

        assertThat(progressions).containsOnlyKeys(TrainingType.values());
        assertThat(progressions.values()).allSatisfy(progression ->
                assertThat(progression.getType()).isEqualTo(progression.type()));
    }

    @Test
    void threePointFocusProgressionExposesTemporaryModifier() {
        var progression = TrainingProgressions.defaultFor(TrainingType.THREE_POINT_FOCUS);

        assertThat(progression.temporaryModifiers())
                .containsExactly(PlayerModifier.nextGameThreePointShotPctBonus(0.05));
    }
}
