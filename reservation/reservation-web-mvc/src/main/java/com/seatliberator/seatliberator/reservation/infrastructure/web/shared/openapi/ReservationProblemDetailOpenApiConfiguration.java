package com.seatliberator.seatliberator.reservation.infrastructure.web.shared.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ReservationProblemDetailOpenApiConfiguration {
    static final String PROBLEM_DETAIL_SCHEMA = "ProblemDetail";
    static final String PROBLEM_DETAIL_REF = "#/components/schemas/" + PROBLEM_DETAIL_SCHEMA;
    static final String PROBLEM_DETAIL_MEDIA_TYPE = "application/problem+json";

    @Bean
    OpenApiCustomizer reservationProblemDetailSchemaCustomizer() {
        return openApi -> {
            var components = openApi.getComponents();
            if (components == null) {
                components = new Components();
                openApi.setComponents(components);
            }

            components.addSchemas(PROBLEM_DETAIL_SCHEMA, problemDetailSchema());
        };
    }

    @Bean
    OperationCustomizer reservationProblemDetailResponseCustomizer() {
        return (operation, handlerMethod) -> {
            var responses = operation.getResponses();
            if (responses == null) return operation;

            responses.forEach((responseCode, response) -> {
                if (isErrorResponseCode(responseCode)) {
                    applyProblemDetailContent(response);
                }
            });

            return operation;
        };
    }

    @SuppressWarnings("unchecked")
    private static Schema<?> problemDetailSchema() {
        return new ObjectSchema()
                .name(PROBLEM_DETAIL_SCHEMA)
                .description("RFC 9457 Problem Detail error response")
                .addProperty("type", new StringSchema()
                        .format("uri")
                        .description("문제 유형을 식별하는 URI")
                        .example("https://seatliberator/errors/bad-request"))
                .addProperty("title", new StringSchema()
                        .description("에러를 식별하는 짧은 제목")
                        .example("BAD_REQUEST"))
                .addProperty("status", new IntegerSchema()
                        .format("int32")
                        .description("HTTP 상태 코드")
                        .example(400))
                .addProperty("detail", new StringSchema()
                        .description("에러 상세 메시지")
                        .example("잘못된 요청입니다."))
                .addProperty("instance", new StringSchema()
                        .format("uri")
                        .description("문제가 발생한 요청 URI")
                        .example("/reservation"))
                .addProperty("code", new StringSchema()
                        .description("애플리케이션 에러 코드")
                        .example("BAD_REQUEST"))
                .required(List.of("type", "title", "status", "detail", "code"));
    }

    private static void applyProblemDetailContent(ApiResponse response) {
        var content = response.getContent();
        if (content == null) {
            content = new Content();
            response.setContent(content);
        }

        content.addMediaType(PROBLEM_DETAIL_MEDIA_TYPE, new MediaType()
                .schema(new Schema<>().$ref(PROBLEM_DETAIL_REF)));
    }

    private static boolean isErrorResponseCode(String responseCode) {
        if (responseCode == null || responseCode.isBlank()) return false;
        if ("default".equals(responseCode)) return true;

        return responseCode.charAt(0) == '4' || responseCode.charAt(0) == '5';
    }
}
