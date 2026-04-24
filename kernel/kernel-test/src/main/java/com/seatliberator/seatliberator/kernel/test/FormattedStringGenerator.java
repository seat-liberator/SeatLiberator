package com.seatliberator.seatliberator.kernel.test;

public class FormattedStringGenerator implements Generator<String> {
    private final String format;
    private final Counter<?> counter;

    public FormattedStringGenerator(String format, Counter<?> counter) {
        if (format == null || format.isBlank()) throw new IllegalArgumentException("format must not be null or blank.");
        if (counter == null) throw new IllegalArgumentException("counter must not be null.");

        this.format = format;
        this.counter = counter;
    }

    public static FormattedStringGenerator of(String format, Counter<?> counter) {
        return new FormattedStringGenerator(format, counter);
    }

    @Override
    public String generate() {
        return String.format(format, counter.next().toString());
    }
}
