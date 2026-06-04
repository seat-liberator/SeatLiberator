package com.seatliberator.seatliberator.starter.launcher.seed;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.List;

@RequiredArgsConstructor
public class ApplicationSeedRunner implements ApplicationRunner {
    private final List<Seeder> seeders;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        seeders.forEach(Seeder::seed);
    }
}
