package com.seatliberator.seatliberator.identity.server.security.authentication.method.token;

import com.seatliberator.seatliberator.identity.server.application.jwks.service.JwtProvider;
import com.seatliberator.seatliberator.identity.server.application.jwks.service.OpaqueTokenProvider;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.token.handler.DefaultTokenIssueProcessor;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.token.handler.TokenIssueProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenConfiguration {
    @Bean
    TokenIssueProcessor tokenIssueProcessor(
            JwtProvider jwtProvider,
            OpaqueTokenProvider opaqueTokenProvider
    ) {
        return new DefaultTokenIssueProcessor(
                jwtProvider,
                opaqueTokenProvider
        );
    }
}
