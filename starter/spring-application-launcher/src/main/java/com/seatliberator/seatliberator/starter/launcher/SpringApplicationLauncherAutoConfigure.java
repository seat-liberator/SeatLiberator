package com.seatliberator.seatliberator.starter.launcher;

import com.seatliberator.seatliberator.starter.launcher.seed.ApplicationSeedRunner;
import com.seatliberator.seatliberator.starter.launcher.seed.Seeder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

@AutoConfiguration
@ConditionalOnProperty(
        prefix = "seatliberator.starter.launcher",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(SpringApplicationLauncherProperties.class)
public class SpringApplicationLauncherAutoConfigure {

    @Bean
    @ConditionalOnProperty(
            prefix = "seatliberator.starter.launcher.seed",
            name = "enabled",
            havingValue = "true"
    )
    @ConditionalOnMissingBean
    ApplicationSeedRunner applicationSeedRunner(List<Seeder> seeders) {
        return new ApplicationSeedRunner(seeders);
    }
}
