package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.progression.training.TrainablePlayerStat;
import com.sanguiwara.progression.training.TrainingProgressions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingProgressionDTOMapperTest {

    private final TrainingProgressionDTOMapperImpl mapper = new TrainingProgressionDTOMapperImpl();

    @BeforeEach
    void setUp() throws Exception {
        setField(mapper, "temporaryModifierDTOMapper", new TemporaryModifierDTOMapperImpl());
    }

    @Test
    void toDto_buildsStatImpactsFromTrainingType() {
        var progression = TrainingProgressions.defaultFor(TrainingType.SHOOTING);

        var dto = mapper.toDto(progression);

        assertThat(dto.statImpacts())
                .extracting("stat")
                .containsExactly(
                        TrainablePlayerStat.TIR_3_PTS,
                        TrainablePlayerStat.TIR_2_PTS,
                        TrainablePlayerStat.LANCER_FRANC,
                        TrainablePlayerStat.FLOATER,
                        TrainablePlayerStat.FINITION_AU_CERCLE
                );
        assertThat(dto.statImpacts())
                .extracting("playerField")
                .containsExactly("tir3Pts", "tir2Pts", "lancerFranc", "floater", "finitionAuCercle");
    }

    @Test
    void toDto_keepsThreePointFocusWithoutStatImpacts() {
        var progression = TrainingProgressions.defaultFor(TrainingType.THREE_POINT_FOCUS);

        var dto = mapper.toDto(progression);

        assertThat(dto.statImpacts()).isEmpty();
        assertThat(dto.temporaryModifiers()).hasSize(1);
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
