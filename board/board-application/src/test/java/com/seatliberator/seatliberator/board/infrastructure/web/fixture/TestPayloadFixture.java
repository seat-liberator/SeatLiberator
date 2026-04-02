package com.seatliberator.seatliberator.board.infrastructure.web.fixture;

import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.atomic.AtomicInteger;

public class TestPayloadFixture {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final AtomicInteger atomicInteger = new AtomicInteger();

    public static TestCreatePostPayload createPostPayload(String categoryId) {
        int cur = atomicInteger.incrementAndGet();
        return createPostPayload(categoryId, "test-post-title-" + cur, "test-post-content-" + cur);
    }

    public static TestCreatePostPayload createPostPayload(String categoryId, String title, String content) {
        return new TestCreatePostPayload(categoryId, title, content);
    }

    public static TestCreateCategoryPayload createCategoryPayload() {
        int cur = atomicInteger.incrementAndGet();
        return createCategoryPayload("test-category-" + cur, "test-category-description");
    }

    public static TestCreateCategoryPayload createCategoryPayload(String name, String description) {
        return new TestCreateCategoryPayload(name, description);
    }

    public static String stringifyPayload(TestPayload payload) {
        return objectMapper.writeValueAsString(payload);
    }

    public interface TestPayload {
    }

    public record TestCreatePostPayload(String categoryId, String title, String content) implements TestPayload {
    }

    public record TestCreateCategoryPayload(String name, String description) implements TestPayload {
    }
}
