package com.seatliberator.seatliberator;

import com.seatliberator.seatliberator.board.web.board.controller.BoardController;
import com.seatliberator.seatliberator.board.web.category.controller.CategoryController;
import com.seatliberator.seatliberator.board.web.comment.controller.CommentController;
import com.seatliberator.seatliberator.board.web.post.controller.PostController;
import com.seatliberator.seatliberator.board.web.user.controller.UserController;
import com.seatliberator.seatliberator.notification.infrastructure.web.controller.NotificationController;
import com.seatliberator.seatliberator.reservation.web.booking.controller.AvailabilityQueryController;
import com.seatliberator.seatliberator.reservation.web.booking.controller.BookingController;
import com.seatliberator.seatliberator.reservation.web.reservation.controller.ReservationQueryController;
import com.seatliberator.seatliberator.reservation.web.reservation.controller.UseReservationController;
import com.seatliberator.seatliberator.reservation.web.seat.controller.SeatCommandController;
import com.seatliberator.seatliberator.reservation.web.waitlist.controller.WaitlistController;
import com.seatliberator.seatliberator.web.jwks.controller.JwksController;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.server.PathContainer;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class GatewayRouteAlignmentTest {
    private static final PathPatternParser PATH_PATTERN_PARSER = new PathPatternParser();

    @Test
    void identityControllerEntrypointsAreCoveredByGatewayRoutes() throws IOException {
        var patterns = gatewayPathPatterns();
        assertCovered(patterns, controllerPaths(JwksController.class), false);
    }

    @Test
    void reservationControllerEntrypointsAreCoveredByGatewayRoutes() throws IOException {
        var patterns = gatewayPathPatterns();
        assertCovered(
                patterns,
                controllerPaths(
                        BookingController.class,
                        AvailabilityQueryController.class,
                        ReservationQueryController.class,
                        UseReservationController.class,
                        SeatCommandController.class,
                        WaitlistController.class
                ),
                false
        );
    }

    @Test
    void boardControllerEntrypointsAreCoveredByGatewayRoutes() throws IOException {
        var patterns = gatewayPathPatterns();
        assertCovered(
                patterns,
                controllerPaths(
                        BoardController.class,
                        CategoryController.class,
                        PostController.class,
                        CommentController.class,
                        UserController.class
                ),
                false
        );
    }

    @Test
    void notificationControllerEntrypointsAreCoveredByGatewayRoutes() throws IOException {
        var patterns = gatewayPathPatterns();
        assertCovered(patterns, controllerPaths(NotificationController.class), true);
    }

    private static void assertCovered(Set<String> gatewayPatterns, Set<String> downstreamPaths, boolean rewriteApiV1Prefix) {
        for (var downstreamPath : downstreamPaths) {
            var externalPath = rewriteApiV1Prefix ? "/api/v1" + downstreamPath : downstreamPath;

            assertThat(gatewayPatterns)
                    .withFailMessage("No gateway path pattern covers downstream path '%s' (external path '%s')", downstreamPath, externalPath)
                    .anyMatch(pattern -> matches(pattern, externalPath));
        }
    }

    private static boolean matches(String pattern, String path) {
        PathPattern pathPattern = PATH_PATTERN_PARSER.parse(pattern);
        return pathPattern.matches(PathContainer.parsePath(path));
    }

    private static Set<String> gatewayPathPatterns() throws IOException {
        var resource = new ClassPathResource("application.yml");
        var content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        var patterns = new LinkedHashSet<String>();

        for (var line : content.split("\\R")) {
            var trimmed = line.trim();
            if (!trimmed.startsWith("- Path=")) continue;

            var values = trimmed.substring("- Path=".length());
            Arrays.stream(values.split(","))
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .forEach(patterns::add);
        }

        return patterns;
    }

    @SafeVarargs
    private static Set<String> controllerPaths(Class<?>... controllerClasses) {
        var paths = new LinkedHashSet<String>();
        for (var controllerClass : controllerClasses) {
            paths.addAll(controllerPaths(controllerClass));
        }
        return paths;
    }

    private static Set<String> controllerPaths(Class<?> controllerClass) {
        var classLevelPaths = requestMappingPaths(controllerClass.getAnnotation(RequestMapping.class));
        if (classLevelPaths.isEmpty()) {
            classLevelPaths = List.of("");
        }

        var paths = new LinkedHashSet<String>();
        for (var method : controllerClass.getDeclaredMethods()) {
            var methodLevelPaths = methodPaths(method);
            if (methodLevelPaths.isEmpty()) {
                continue;
            }

            for (var classLevelPath : classLevelPaths) {
                for (var methodLevelPath : methodLevelPaths) {
                    paths.add(normalizePath(classLevelPath, methodLevelPath));
                }
            }
        }
        return paths;
    }

    private static List<String> methodPaths(java.lang.reflect.Method method) {
        var paths = new ArrayList<String>();
        paths.addAll(requestMappingPaths(method.getAnnotation(RequestMapping.class)));
        paths.addAll(mappingPaths(method.getAnnotation(GetMapping.class)));
        paths.addAll(mappingPaths(method.getAnnotation(PostMapping.class)));
        paths.addAll(mappingPaths(method.getAnnotation(PutMapping.class)));
        paths.addAll(mappingPaths(method.getAnnotation(PatchMapping.class)));
        paths.addAll(mappingPaths(method.getAnnotation(DeleteMapping.class)));

        if (paths.isEmpty()) {
            return List.of();
        }

        if (paths.stream().allMatch(String::isEmpty)) {
            return List.of("");
        }

        return paths;
    }

    private static List<String> requestMappingPaths(RequestMapping mapping) {
        if (mapping == null) {
            return List.of();
        }

        if (mapping.path().length > 0) {
            return Arrays.asList(mapping.path());
        }

        if (mapping.value().length > 0) {
            return Arrays.asList(mapping.value());
        }

        return List.of("");
    }

    private static List<String> mappingPaths(GetMapping mapping) {
        if (mapping == null) {
            return List.of();
        }
        return annotationPaths(mapping.path(), mapping.value());
    }

    private static List<String> mappingPaths(PostMapping mapping) {
        if (mapping == null) {
            return List.of();
        }
        return annotationPaths(mapping.path(), mapping.value());
    }

    private static List<String> mappingPaths(PutMapping mapping) {
        if (mapping == null) {
            return List.of();
        }
        return annotationPaths(mapping.path(), mapping.value());
    }

    private static List<String> mappingPaths(PatchMapping mapping) {
        if (mapping == null) {
            return List.of();
        }
        return annotationPaths(mapping.path(), mapping.value());
    }

    private static List<String> mappingPaths(DeleteMapping mapping) {
        if (mapping == null) {
            return List.of();
        }
        return annotationPaths(mapping.path(), mapping.value());
    }

    private static List<String> annotationPaths(String[] path, String[] value) {
        if (path.length > 0) {
            return Arrays.asList(path);
        }

        if (value.length > 0) {
            return Arrays.asList(value);
        }

        return List.of("");
    }

    private static String normalizePath(String classLevelPath, String methodLevelPath) {
        var classPath = StringUtils.hasText(classLevelPath) ? classLevelPath : "";
        var methodPath = StringUtils.hasText(methodLevelPath) ? methodLevelPath : "";

        if (!StringUtils.hasText(classPath) && !StringUtils.hasText(methodPath)) {
            return "/";
        }

        if (!StringUtils.hasText(classPath)) {
            return methodPath;
        }

        if (!StringUtils.hasText(methodPath)) {
            return classPath;
        }

        if (classPath.endsWith("/") && methodPath.startsWith("/")) {
            return classPath.substring(0, classPath.length() - 1) + methodPath;
        }

        if (!classPath.endsWith("/") && !methodPath.startsWith("/")) {
            return classPath + "/" + methodPath;
        }

        return classPath + methodPath;
    }
}
