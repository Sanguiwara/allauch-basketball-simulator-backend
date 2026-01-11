package com.sanguiwara.configuration;

import com.sanguiwara.factory.GamePlanFactory;
import com.sanguiwara.factory.PlayerFactory;
import com.sanguiwara.factory.TeamFactory;
import com.sanguiwara.service.GameCalculator;
import com.sanguiwara.service.simulator.DriveSimulator;
import com.sanguiwara.service.simulator.TwoPointSimulator;
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
    public TwoPointSimulator twoPointSimulator(Random random){
        return new TwoPointSimulator(random);
    }

    @Bean
    public DriveSimulator driveSimulator(Random random){
        return new DriveSimulator(random);
    }

    @Bean
    public GameCalculator gameCalculator(TwoPointSimulator twoPointSimulator,
                                         DriveSimulator driveSimulator) {
        return new GameCalculator(twoPointSimulator, driveSimulator);
    }
}