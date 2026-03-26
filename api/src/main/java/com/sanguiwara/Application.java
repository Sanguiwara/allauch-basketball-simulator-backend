package com.sanguiwara;

import com.sanguiwara.timeevent.EventManager;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    ApplicationRunner loadTimeEventsAtStartup(EventManager eventManager) {
        return args -> eventManager.loadEventsFromDatabase();
    }

}
