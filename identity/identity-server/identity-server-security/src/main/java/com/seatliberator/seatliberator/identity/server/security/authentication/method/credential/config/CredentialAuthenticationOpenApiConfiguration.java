package com.seatliberator.seatliberator.identity.server.security.authentication.method.credential.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@SuppressWarnings("unchecked")
public class CredentialAuthenticationOpenApiConfiguration {
    private static final String SIGN_IN_SCHEMA = "CredentialSignInRequest";
    private static final String SIGN_UP_SCHEMA = "CredentialSignUpRequest";
    private static final String ISSUED_TOKEN_SCHEMA = "IssuedTokenEntry";

    @Bean
    OpenApiCustomizer credentialAuthenticationOpenApiCustomizer(
            CredentialAuthenticationConfigurationProperties properties
    ) {
        return openApi -> {
            applySchemas(openApi);
            applyCredentialPath(
                    openApi,
                    properties.signIn(),
                    "이메일 로그인",
                    "이메일과 비밀번호로 로그인하고 토큰을 발급합니다.",
                    SIGN_IN_SCHEMA
            );
            applyCredentialPath(
                    openApi,
                    properties.signUp(),
                    "이메일 회원가입",
                    "닉네임, 이메일, 비밀번호로 계정을 생성하고 토큰을 발급합니다.",
                    SIGN_UP_SCHEMA
            );
        };
    }

    private void applyCredentialPath(
            OpenAPI openApi,
            Endpoint endpoint,
            String summary,
            String description,
            String requestSchemaName
    ) {
        var operation = new Operation()
                .addTagsItem("Authentication")
                .summary(summary)
                .description(description)
                .requestBody(jsonRequestBody(requestSchemaName))
                .responses(new ApiResponses()
                        .addApiResponse("200", jsonResponse("인증 성공", ISSUED_TOKEN_SCHEMA))
                        .addApiResponse("400", new ApiResponse().description("잘못된 요청"))
                        .addApiResponse("401", new ApiResponse().description("인증 실패")));

        var item = new PathItem();
        if ("POST".equalsIgnoreCase(endpoint.method())) {
            item.post(operation);
        }

        paths(openApi).addPathItem(endpoint.uri(), item);
    }

    private RequestBody jsonRequestBody(String schemaName) {
        return new RequestBody()
                .required(true)
                .content(new Content().addMediaType(
                        org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                        new MediaType().schema(ref(schemaName))
                ));
    }

    private ApiResponse jsonResponse(String description, String schemaName) {
        return new ApiResponse()
                .description(description)
                .content(new Content().addMediaType(
                        org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                        new MediaType().schema(ref(schemaName))
                ));
    }

    private void applySchemas(OpenAPI openApi) {
        components(openApi)
                .addSchemas(SIGN_IN_SCHEMA, signInSchema())
                .addSchemas(SIGN_UP_SCHEMA, signUpSchema())
                .addSchemas(ISSUED_TOKEN_SCHEMA, issuedTokenSchema());
    }

    private Schema<?> signInSchema() {
        return new ObjectSchema()
                .description("이메일 인증 로그인 요청")
                .addProperty("email", new StringSchema().description("로그인 이메일").example("user@example.com"))
                .addProperty("password", new StringSchema().description("로그인 비밀번호").example("password1234!"))
                .required(List.of("email", "password"));
    }

    private Schema<?> signUpSchema() {
        return new ObjectSchema()
                .description("이메일 인증 회원가입 요청")
                .addProperty("nickname", new StringSchema().description("사용자 닉네임").example("lilamaris"))
                .addProperty("email", new StringSchema().description("로그인에 사용할 이메일").example("user@example.com"))
                .addProperty("password", new StringSchema().description("로그인에 사용할 비밀번호").example("password1234!"))
                .required(List.of("nickname", "email", "password"));
    }

    private Schema<?> issuedTokenSchema() {
        return new ObjectSchema()
                .description("인증 성공 후 발급된 토큰")
                .addProperty("accessToken", new StringSchema().description("API 인증에 사용할 액세스 토큰"))
                .addProperty("refreshToken", new StringSchema().description("액세스 토큰 재발급에 사용할 리프레시 토큰"))
                .required(List.of("accessToken", "refreshToken"));
    }

    private Schema<?> ref(String schemaName) {
        return new Schema<>().$ref("#/components/schemas/" + schemaName);
    }

    private Components components(OpenAPI openApi) {
        if (openApi.getComponents() == null) {
            openApi.setComponents(new Components());
        }
        return openApi.getComponents();
    }

    private Paths paths(OpenAPI openApi) {
        if (openApi.getPaths() == null) {
            openApi.setPaths(new Paths());
        }
        return openApi.getPaths();
    }
}
