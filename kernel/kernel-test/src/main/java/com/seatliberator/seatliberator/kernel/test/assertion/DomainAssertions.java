package com.seatliberator.seatliberator.kernel.test.assertion;

import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;

public final class DomainAssertions {
    public static DomainThrowableAssert assertThatDomainThrownBy(ThrowingCallable callable) {
        return new DomainThrowableAssert(Assertions.catchThrowable(callable));
    }

    public static final class DomainThrowableAssert extends AbstractThrowableAssert<DomainThrowableAssert, Throwable> {
        private DomainThrowableAssert(Throwable actual) {
            super(actual, DomainThrowableAssert.class);
        }

        public DomainThrowableAssert hasNonNullMessage() {
            hasMessageContaining("must not be null");
            return this;
        }

        public DomainThrowableAssert hasNonNullMessageFor(String fieldName) {
            hasMessageContaining(fieldName);
            hasMessageContaining("must not be null");
            return this;
        }

        public DomainThrowableAssert hasNonBlankMessage() {
            hasMessageContaining("must not be blank");
            return this;
        }

        public DomainThrowableAssert hasNonBlankMessageFor(String fieldName) {
            hasMessageContaining(fieldName);
            hasMessageContaining("must not be blank");
            return this;
        }

        public DomainThrowableAssert hasNonEmptyMessage() {
            hasMessageContaining("must not be empty");
            return this;
        }

        public DomainThrowableAssert hasNonEmptyMessageFor(String fieldName) {
            hasMessageContaining(fieldName);
            hasMessageContaining("must not be empty");
            return this;
        }

        public DomainThrowableAssert hasNonNullElementMessage() {
            hasMessageContaining("must not contain null");
            return this;
        }

        public DomainThrowableAssert hasNonNullElementMessageFor(String fieldName) {
            hasMessageContaining(fieldName);
            hasMessageContaining("must not contain null");
            return this;
        }

        public DomainThrowableAssert hasNegativeMessage() {
            hasMessageContaining("must be negative");
            return this;
        }

        public DomainThrowableAssert hasNegativeMessageFor(String fieldName) {
            hasMessageContaining(fieldName);
            hasMessageContaining("must be negative");
            return this;
        }

        public DomainThrowableAssert hasNonNegativeMessage() {
            hasMessageContaining("must be non-negative");
            return this;
        }

        public DomainThrowableAssert hasNonNegativeMessageFor(String fieldName) {
            hasMessageContaining(fieldName);
            hasMessageContaining("must be non-negative");
            return this;
        }

        public DomainThrowableAssert hasPositiveMessage() {
            hasMessageContaining("must be positive");
            return this;
        }

        public DomainThrowableAssert hasPositiveMessageFor(String fieldName) {
            hasMessageContaining(fieldName);
            hasMessageContaining("must be positive");
            return this;
        }

        public DomainThrowableAssert hasNonPositiveMessage() {
            hasMessageContaining("must be non-positive");
            return this;
        }

        public DomainThrowableAssert hasNonPositiveMessageFor(String fieldName) {
            hasMessageContaining(fieldName);
            hasMessageContaining("must be non-positive");
            return this;
        }
    }
}
