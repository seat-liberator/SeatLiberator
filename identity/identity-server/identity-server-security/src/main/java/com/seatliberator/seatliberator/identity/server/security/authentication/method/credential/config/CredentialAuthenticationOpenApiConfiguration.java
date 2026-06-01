package com.seatliberator.seatliberator.identity.server.security.authentication.method.credential.config;

import com.seatliberator.seatliberator.identity.server.security.authentication.method.credential.filter.CredentialSignInRequest;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.credential.filter.CredentialSignUpRequest;
import com.seatliberator.seatliberator.identity.server.security.shared.response.TokenPayload;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CredentialAuthenticationOpenApiConfiguration {
    private final static String SIGN_IN_SCHEMA_KEY = "CredentialSignInRequest";
    private final static String SIGN_UP_SCHEMA_KEY = "CredentialSignUpRequest";
    private final static String TOKEN_SCHEMA_KEY = "TokenPayload";

    private final static List<ApiOperation> CREDENTIAL_OPERATIONS = List.of(
            new ApiOperation("/api/v1/auth/sign-in", "이메일 로그인", "이메일과 비밀번호로 로그인하고 토큰을 발급합니다.", SIGN_IN_SCHEMA_KEY),
            new ApiOperation("/api/v1/auth/sign-up", "이메일 회원가입", "닉네임, 이메일, 비밀번호로 계정을 생성하고 토큰을 발급합니다.", SIGN_UP_SCHEMA_KEY)
    );

    @Bean
    OpenApiCustomizer credentialOpenApiCustomizer() {
        return openApi -> {
            applySchema(openApi);
            applyPaths(openApi);
        };
    }

    private void applyPaths(OpenAPI openApi) {
        for (var op : CREDENTIAL_OPERATIONS) {
            var path = new PathItem();
            var operation = new Operation()
                    .addTagsItem("Auth")
                    .summary(op.summary())
                    .description(op.description())
                    .requestBody(jsonRequestBody(op.requestSchemaKey()))
                    .responses(getDefaultApiResponses());
            path.post(operation);
            getPaths(openApi).addPathItem(op.path(), path);
        }
    }

    private ApiResponses getDefaultApiResponses() {
        return new ApiResponses()
                .addApiResponse("200", jsonResponse("인증 성공", TOKEN_SCHEMA_KEY))
                .addApiResponse("400", new ApiResponse().description("잘못된 요청"))
                .addApiResponse("401", new ApiResponse().description("인증 실패"));
    }

    private RequestBody jsonRequestBody(String schemaKey) {
        var content = new Content().addMediaType("application/json", schemaRef(schemaKey));
        return new RequestBody()
                .required(true)
                .content(content);
    }

    private ApiResponse jsonResponse(String description, String schemaKey) {
        var content = new Content().addMediaType("application/json", schemaRef(schemaKey));
        return new ApiResponse()
                .description(description)
                .content(content);
    }

    private MediaType schemaRef(String schemaKey) {
        var ref = new Schema<>().$ref("#/components/schemas/" + schemaKey);
        return new MediaType().schema(ref);
    }

    private void applySchema(OpenAPI openApi) {
        var credentialSignInSchema = resolveSchema(CredentialSignInRequest.class);
        var credentialSignUpSchema = resolveSchema(CredentialSignUpRequest.class);
        var tokenPayloadSchema = resolveSchema(TokenPayload.class);

        getComponents(openApi)
                .addSchemas(SIGN_IN_SCHEMA_KEY, credentialSignInSchema)
                .addSchemas(SIGN_UP_SCHEMA_KEY, credentialSignUpSchema)
                .addSchemas(TOKEN_SCHEMA_KEY, tokenPayloadSchema);
    }

    private Schema<?> resolveSchema(Class<?> schema) {
        return ModelConverters.getInstance().readAllAsResolvedSchema(schema).schema;
    }

    private Components getComponents(OpenAPI openApi) {
        if (openApi.getComponents() == null) {
            openApi.setComponents(new Components());
        }
        return openApi.getComponents();
    }

    private Paths getPaths(OpenAPI openApi) {
        if (openApi.getPaths() == null) {
            openApi.setPaths(new Paths());
        }
        return openApi.getPaths();
    }

    private record ApiOperation(String path, String summary, String description, String requestSchemaKey) {
    }
}
