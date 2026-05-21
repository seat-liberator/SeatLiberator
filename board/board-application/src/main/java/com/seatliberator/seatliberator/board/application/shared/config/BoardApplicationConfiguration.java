package com.seatliberator.seatliberator.board.application.shared.config;

import com.seatliberator.seatliberator.board.api.BoardApi;
import com.seatliberator.seatliberator.kernel.FixedCurrentApplicationNamespaceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BoardApplicationConfiguration {

    @Bean
    FixedCurrentApplicationNamespaceProvider fixedCurrentApplicationNamespaceProvider() {
        return new FixedCurrentApplicationNamespaceProvider(BoardApi.NAMESPACE);
    }
}
