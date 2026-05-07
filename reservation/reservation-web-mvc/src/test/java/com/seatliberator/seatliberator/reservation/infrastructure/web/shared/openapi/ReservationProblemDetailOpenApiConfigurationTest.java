package com.seatliberator.seatliberator.reservation.infrastructure.web.shared.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.seatliberator.seatliberator.reservation.infrastructure.web.shared.openapi.ReservationProblemDetailOpenApiConfiguration.PROBLEM_DETAIL_MEDIA_TYPE;
import static com.seatliberator.seatliberator.reservation.infrastructure.web.shared.openapi.ReservationProblemDetailOpenApiConfiguration.PROBLEM_DETAIL_REF;
import static com.seatliberator.seatliberator.reservation.infrastructure.web.shared.openapi.ReservationProblemDetailOpenApiConfiguration.PROBLEM_DETAIL_SCHEMA;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Reservation ProblemDetail OpenAPI Configuration")
class ReservationProblemDetailOpenApiConfigurationTest {
    private final ReservationProblemDetailOpenApiConfiguration configuration =
            new ReservationProblemDetailOpenApiConfiguration();

    @Test
    @DisplayName("ProblemDetail schema를 OpenAPI components에 등록한다")
    @SuppressWarnings("unchecked")
    void register_problem_detail_schema() {
        var openApi = new OpenAPI();

        configuration.reservationProblemDetailSchemaCustomizer().customise(openApi);

        var schema = openApi.getComponents().getSchemas().get(PROBLEM_DETAIL_SCHEMA);

        assertThat(schema).isNotNull();
        assertThat(schema.getProperties())
                .containsKeys("type", "title", "status", "detail", "instance", "code");
        assertThat(schema.getRequired())
                .containsExactly("type", "title", "status", "detail", "code");
    }

    @Test
    @DisplayName("4xx, 5xx 응답에 ProblemDetail content를 추가한다")
    void apply_problem_detail_content_to_error_responses() {
        var operation = new Operation()
                .responses(new ApiResponses()
                        .addApiResponse("200", new ApiResponse().description("조회 성공"))
                        .addApiResponse("400", new ApiResponse().description("잘못된 요청"))
                        .addApiResponse("500", new ApiResponse().description("서버 오류")));

        configuration.reservationProblemDetailResponseCustomizer()
                .customize(operation, null);

        var success = operation.getResponses().get("200");
        var badRequest = operation.getResponses().get("400");
        var internalServerError = operation.getResponses().get("500");

        assertThat(success.getContent()).isNull();
        assertProblemDetailResponse(badRequest);
        assertProblemDetailResponse(internalServerError);
    }

    private static void assertProblemDetailResponse(ApiResponse response) {
        var problemDetail = response.getContent().get(PROBLEM_DETAIL_MEDIA_TYPE);

        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getSchema().get$ref())
                .isEqualTo(PROBLEM_DETAIL_REF);
    }
}
