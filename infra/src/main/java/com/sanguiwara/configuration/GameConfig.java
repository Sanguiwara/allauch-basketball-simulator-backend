package com.sanguiwara.configuration;

import com.sanguiwara.calculator.*;
import com.sanguiwara.calculator.spec.DriveSpecification;
import com.sanguiwara.calculator.spec.ThreePointSpecification;
import com.sanguiwara.calculator.spec.TwoPointSpecification;
import com.sanguiwara.badges.BadgeEngine;
import com.sanguiwara.defense.*;
import com.sanguiwara.factory.PlayerGenerator;
import com.sanguiwara.factory.TeamFactory;
import com.sanguiwara.gameevent.DriveEvent;
import com.sanguiwara.gameevent.ThreePointShotEvent;
import com.sanguiwara.gameevent.TwoPointShotEvent;
import com.sanguiwara.progression.manager.InactivityProgressionManager;
import com.sanguiwara.progression.manager.MoraleProgressionManager;
import com.sanguiwara.progression.manager.ReboundingProgressionManager;
import com.sanguiwara.progression.manager.ShootingSkillProgressionManager;
import com.sanguiwara.progression.manager.StocksProgressionManager;
import com.sanguiwara.progression.manager.TrainingProgressionManager;
import com.sanguiwara.result.DriveResult;
import com.sanguiwara.result.ThreePointShootingResult;
import com.sanguiwara.result.TwoPointShootingResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Random;

@Configuration
public class GameConfig {

    @Bean
    public Random random(@Value("${sim.seed:0}") long seed) {
        return seed == 0 ? new Random() : new Random(seed);
    }

    @Bean
    public PlayerGenerator playerFactory(Random random) { return new PlayerGenerator(random);}

    @Bean
    public TeamFactory teamFactory() { return new TeamFactory();}

    @Bean
    public BadgeEngine badgeEngine() {
        return new BadgeEngine();
    }

    @Bean
    public DefenseSchemeResolver defenseSchemeResolver(BadgeEngine badgeEngine) {
        List<DefensiveScheme> schemes = List.of(
                new RegularMan2ManScheme(badgeEngine),
                new Zone23Scheme(badgeEngine),
                new Zone212Scheme(badgeEngine),
                new Zone32Scheme(badgeEngine)
        );

        return new DefenseSchemeResolver(schemes);}

    @Bean
    public ThreePointSpecification threePointSpecification(Random random, BadgeEngine badgeEngine) {
        return new ThreePointSpecification(random, badgeEngine);
    }

    @Bean
    public TwoPointSpecification twoPointSpecification(Random random, BadgeEngine badgeEngine) {
        return new TwoPointSpecification(random, badgeEngine);
    }

    @Bean
    public DriveSpecification driveSpecification(Random random, BadgeEngine badgeEngine) {
        return new DriveSpecification(random, badgeEngine);
    }

    @Bean
    public AssistCalculator playmakingCalculator(DefenseSchemeResolver defenseSchemeResolver) { return  new AssistCalculator(defenseSchemeResolver);}

    @Bean
    public StealSimulator stealSimulator(Random random, BadgeEngine badgeEngine) { return new StealSimulator(random, badgeEngine);}

    @Bean
    public ShotSimulator<ThreePointShotEvent, ThreePointShootingResult> threePointSimulator(
            Random random,
            ThreePointSpecification spec,
            DefenseSchemeResolver defenseSchemeResolver) {
        return new ShotSimulator<>( random, spec, defenseSchemeResolver);
    }

    @Bean
    public ShotSimulator<TwoPointShotEvent, TwoPointShootingResult> twoPointSimulator(
            Random random,
            TwoPointSpecification spec,
            DefenseSchemeResolver defenseSchemeResolver) {
        return new ShotSimulator<>( random, spec, defenseSchemeResolver);
    }

    @Bean
    public ShotSimulator<DriveEvent, DriveResult> driveSimulator(
            Random random,
            DriveSpecification spec,
            DefenseSchemeResolver defenseSchemeResolver) {
        return new ShotSimulator<>(random,  spec, defenseSchemeResolver);
    }
    @Bean
    public ReboundCalculator reboundCalculator(Random random, BadgeEngine badgeEngine) { return new ReboundCalculator(random, badgeEngine);}

    @Bean
    public BlockCalculator blockCalculator(){ return new BlockCalculator();}

    @Bean
    public GameSimulator gameCalculator(ShotSimulator<TwoPointShotEvent, TwoPointShootingResult> twoPointSimulator,
                                        ShotSimulator<DriveEvent, DriveResult> driveSimulator,
                                        ShotSimulator<ThreePointShotEvent, ThreePointShootingResult> threePointSimulator,
                                        AssistCalculator assistCalculator,
                                        ReboundCalculator reboundCalculator,
                                        BlockCalculator blockCalculator,
                                        StealSimulator stealSimulator
    ) {
        return new GameSimulator(threePointSimulator, twoPointSimulator, driveSimulator, assistCalculator, reboundCalculator, blockCalculator, stealSimulator);
    }

    @Bean
    public ShootingSkillProgressionManager shootingSkillProgressionManager(Random random) {
        return new ShootingSkillProgressionManager(random);
    }

    @Bean
    public InactivityProgressionManager inactivityProgressionManager() {
        return new InactivityProgressionManager();
    }

    @Bean
    public ReboundingProgressionManager reboundingProgressionManager(Random random) {
        return new ReboundingProgressionManager(random);
    }

    @Bean
    public StocksProgressionManager stocksProgressionManager(Random random) {
        return new StocksProgressionManager(random);
    }

    @Bean
    public MoraleProgressionManager moraleProgressionManager(Random random) {
        return new MoraleProgressionManager(random);
    }

    @Bean
    public TrainingProgressionManager trainingProgressionManager(Random random) {
        return new TrainingProgressionManager(random);
    }

}
