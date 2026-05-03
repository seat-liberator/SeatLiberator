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
    }
}
