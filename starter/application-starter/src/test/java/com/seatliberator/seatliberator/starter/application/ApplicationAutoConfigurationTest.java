package com.seatliberator.seatliberator.starter.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.time.Clock;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ApplicationAutoConfiguration 테스트")
public class ApplicationAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ApplicationAutoConfiguration.class));

    @Test
    @DisplayName("application 기본 bean을 등록한다")
    void register_default_application_beans() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(Clock.class);
        });
    }

    @Test
    @DisplayName("timezone 설정으로 Clock zone을 구성한다")
    void configures_clock_zone_from_property() {
        contextRunner
                .withPropertyValues("seatliberator.starter.application.timezone=Asia/Seoul")
                .run(context -> assertThat(context.getBean(Clock.class).getZone())
                        .isEqualTo(ZoneId.of("Asia/Seoul")));
    }

    @Test
    @DisplayName("이미 등록된 bean이 있으면 기본 bean으로 덮어쓰지 않는다")
    void does_not_override_existing_beans() {
        var customClock = Clock.system(ZoneId.of("Asia/Seoul"));

        contextRunner
                .withBean(Clock.class, () -> customClock)
                .run(context -> {
                    assertThat(context.getBean(Clock.class)).isSameAs(customClock);
                });
    }

    @Test
    @DisplayName("seatliberator.starter.application.enabled가 false이면 자동 설정하지 않는다")
    void does_not_register_beans_when_application_starter_is_disabled() {
        contextRunner
                .withPropertyValues("seatliberator.starter.application.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(Clock.class);
                });
    }
}
