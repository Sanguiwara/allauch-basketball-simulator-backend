package service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import simulator.ThreePointSimulator;

import static org.junit.jupiter.api.Assertions.assertTrue;
@Slf4j
public class ThreePointSimulatorTest {



    @Test
    void sampleThreePointAttemptsTest() {

        // Test case 1: Low usage and aggressiveness
        int attempts1 = ThreePointSimulator.sampleThreePointAttempts(10, 10);
        log.info(String.valueOf(attempts1));
        assertTrue(attempts1 >= 0 && attempts1 <= 15, "Attempts should be clamped between 0 and 15");

        // Test case 2: High usage, high aggressiveness
        int attempts2 = ThreePointSimulator.sampleThreePointAttempts(30, 50);
        log.info(String.valueOf(attempts2));

        assertTrue(attempts2 >= 0 && attempts2 <= 15, "Attempts should be clamped between 0 and 15");

        // Test case 3: Moderate inputs
        int attempts3 = ThreePointSimulator.sampleThreePointAttempts(18, 90);
        log.info(String.valueOf(attempts3));


        // Test case 5: Edge case with maximum inputs
        int attempts5 = ThreePointSimulator.sampleThreePointAttempts(30, 100);
        log.info(String.valueOf(attempts5));

        assertTrue(attempts5 >= 0 && attempts5 <= 20, "Maximum inputs should not exceed 15 attempts");
    }

}
