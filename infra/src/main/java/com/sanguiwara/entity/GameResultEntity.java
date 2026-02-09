package com.sanguiwara.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "game_results")
public class GameResultEntity {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false, unique = true)
    private GameEntity game;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "threePointShootingResult.attempts", column = @Column(name = "home_three_pt_attempts", nullable = false)),
            @AttributeOverride(name = "threePointShootingResult.made", column = @Column(name = "home_three_pt_made", nullable = false)),
            @AttributeOverride(name = "driveResult.attempts", column = @Column(name = "home_drive_attempts", nullable = false)),
            @AttributeOverride(name = "driveResult.made", column = @Column(name = "home_drive_made", nullable = false)),
            @AttributeOverride(name = "driveResult.foulsDrawn", column = @Column(name = "home_drive_fouls_drawn", nullable = false)),
            @AttributeOverride(name = "twoPointShootingResult.attempts", column = @Column(name = "home_two_pt_attempts", nullable = false)),
            @AttributeOverride(name = "twoPointShootingResult.made", column = @Column(name = "home_two_pt_made", nullable = false)),
    })
    private BoxScoreEmbeddable homeScore = new BoxScoreEmbeddable();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "threePointShootingResult.attempts", column = @Column(name = "away_three_pt_attempts", nullable = false)),
            @AttributeOverride(name = "threePointShootingResult.made", column = @Column(name = "away_three_pt_made", nullable = false)),
            @AttributeOverride(name = "driveResult.attempts", column = @Column(name = "away_drive_attempts", nullable = false)),
            @AttributeOverride(name = "driveResult.made", column = @Column(name = "away_drive_made", nullable = false)),
            @AttributeOverride(name = "driveResult.foulsDrawn", column = @Column(name = "away_drive_fouls_drawn", nullable = false)),
            @AttributeOverride(name = "twoPointShootingResult.attempts", column = @Column(name = "away_two_pt_attempts", nullable = false)),
            @AttributeOverride(name = "twoPointShootingResult.made", column = @Column(name = "away_two_pt_made", nullable = false)),
    })
    private BoxScoreEmbeddable awayScore = new BoxScoreEmbeddable();

    @Getter
    @Setter
    @NoArgsConstructor
    @Embeddable
    public static class BoxScoreEmbeddable {

        @Embedded
        @AttributeOverrides({
                @AttributeOverride(name = "attempts", column = @Column(name = "three_pt_attempts", nullable = false)),
                @AttributeOverride(name = "made", column = @Column(name = "three_pt_made", nullable = false)),
        })
        private ShootingResultEmbeddable threePointShootingResult = new ShootingResultEmbeddable();

        @Embedded
        @AttributeOverrides({
                @AttributeOverride(name = "attempts", column = @Column(name = "drive_attempts", nullable = false)),
                @AttributeOverride(name = "made", column = @Column(name = "drive_made", nullable = false)),
                @AttributeOverride(name = "foulsDrawn", column = @Column(name = "drive_fouls_drawn", nullable = false)),
        })
        private DriveResultEmbeddable driveResult = new DriveResultEmbeddable();

        @Embedded
        @AttributeOverrides({
                @AttributeOverride(name = "attempts", column = @Column(name = "two_pt_attempts", nullable = false)),
                @AttributeOverride(name = "made", column = @Column(name = "two_pt_made", nullable = false)),
        })
        private ShootingResultEmbeddable twoPointShootingResult = new ShootingResultEmbeddable();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @Embeddable
    public static class ShootingResultEmbeddable {

        @Column(name = "attempts", nullable = false)
        private int attempts;

        @Column(name = "made", nullable = false)
        private int made;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @Embeddable
    public static class DriveResultEmbeddable {

        @Column(name = "attempts", nullable = false)
        private int attempts;

        @Column(name = "made", nullable = false)
        private int made;

        @Column(name = "fouls_drawn", nullable = false)
        private int foulsDrawn;
    }
}

