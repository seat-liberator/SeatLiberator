package com.seatliberator.seatliberator.kernel.test.assertion;

import com.seatliberator.seatliberator.kernel.exception.ErrorCode;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;

import static org.assertj.core.api.Assertions.catchThrowable;

public class ApplicationAssertions {
    public static ApplicationThrowableAssert assertThatApplicationThrownBy(ThrowingCallable callable) {
        return new ApplicationThrowableAssert(catchThrowable(callable));
    }

    public static final class ApplicationThrowableAssert extends AbstractThrowableAssert<ApplicationThrowableAssert, Throwable> {
        private ApplicationThrowableAssert(Throwable actual) {
            super(actual, ApplicationThrowableAssert.class);
        }

        public ApplicationThrowableAssert hasErrorCode(ErrorCode errorCode) {
            extracting("errorCode")
                    .isEqualTo(errorCode);
            return this;
        }
    }
}
