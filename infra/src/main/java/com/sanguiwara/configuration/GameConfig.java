package com.sanguiwara.configuration;

import com.sanguiwara.calculator.AssistManager;
import com.sanguiwara.calculator.ShotSimulator;
import com.sanguiwara.calculator.spec.DriveSpecification;
import com.sanguiwara.calculator.spec.ThreePointSpecification;
import com.sanguiwara.calculator.spec.TwoPointSpecification;
import com.sanguiwara.factory.GamePlanFactory;
import com.sanguiwara.factory.PlayerFactory;
import com.sanguiwara.factory.TeamFactory;
import com.sanguiwara.gameevent.DriveEvent;
import com.sanguiwara.gameevent.ThreePointShotEvent;
import com.sanguiwara.gameevent.TwoPointShotEvent;
import com.sanguiwara.result.DriveResult;
import com.sanguiwara.result.ThreePointShootingResult;
import com.sanguiwara.result.TwoPointShootingResult;
import com.sanguiwara.calculator.GameCalculator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class GameConfig {

    @Bean
    public Random random(@Value("${sim.seed:0}") long seed) {
        return seed == 0 ? new Random() : new Random(seed);
    }

    @Bean
    public PlayerFactory playerFactory(Random random) { return new PlayerFactory(random);}

    @Bean
    public TeamFactory teamFactory() { return new TeamFactory();}

    @Bean
    public GamePlanFactory gamePlanFactory() { return new GamePlanFactory();}

    @Bean
    public ThreePointSpecification threePointSpecification(Random random) {
        return new ThreePointSpecification(random);
    }

    @Bean
    public TwoPointSpecification twoPointSpecification(Random random) {
        return new TwoPointSpecification(random);
    }

    @Bean
    public DriveSpecification driveSpecification(Random random) {
        return new DriveSpecification(random);
    }

    @Bean
    public AssistManager assistManager(Random random) { return new AssistManager(random);}

    @Bean
    public ShotSimulator<ThreePointShotEvent, ThreePointShootingResult> threePointSimulator(
            AssistManager assistManager,
            Random random,
            ThreePointSpecification spec) {
        return new ShotSimulator<>(assistManager, random, spec);
    }

    @Bean
    public ShotSimulator<TwoPointShotEvent, TwoPointShootingResult> twoPointSimulator(
            AssistManager assistManager,
            Random random,
            TwoPointSpecification spec) {
        return new ShotSimulator<>(assistManager, random, spec);
    }

    @Bean
    public ShotSimulator<DriveEvent, DriveResult> driveSimulator(
            AssistManager assistManager,
            Random random,
            DriveSpecification spec) {
        return new ShotSimulator<>(assistManager, random,  spec);
    }

    @Bean
    public GameCalculator gameCalculator(ShotSimulator<TwoPointShotEvent, TwoPointShootingResult> twoPointSimulator,
                                         ShotSimulator<DriveEvent, DriveResult> driveSimulator,
                                         ShotSimulator<ThreePointShotEvent, ThreePointShootingResult> threePointSimulator) {
        return new GameCalculator(threePointSimulator, twoPointSimulator, driveSimulator);
    }


}