package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.token;

import com.seatliberator.seatliberator.identity.core.token.TokenProvider;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.token.handler.DefaultTokenIssueProcessor;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.token.handler.TokenIssueProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenConfiguration {
    @Bean
    TokenIssueProcessor tokenIssueProcessor(
            TokenProvider jwtProvider,
            TokenProvider opaqueTokenProvider
    ) {
        return new DefaultTokenIssueProcessor(
                jwtProvider,
                opaqueTokenProvider
        );
    }
}
