package com.seatliberator.seatliberator.reservation.shared.application.seed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("local")
@ConditionalOnProperty(
        prefix = "app.seed",
        name = "enabled",
        havingValue = "true"
)
@RequiredArgsConstructor
public class ApplicationSeedRunner implements ApplicationRunner {
    private final SeatSeedService seatSeedService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Seed runner enabled.");
        seatSeedService.seed();
    }
}
