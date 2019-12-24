package com.matthisk.flow.channels;

import java.util.Objects;

public class Envelope<T> {

    private final Throwable cause;

    private final boolean isComplete;

    private final T value;

    private Envelope(T value, Throwable cause, boolean isComplete) {
        this.cause = cause;
        this.isComplete = isComplete;
        this.value = value;
    }

    public Throwable getCause() {
        return cause;
    }

    public T getValue() {
        return value;
    }
    public boolean isComplete() {
        return isComplete;
    }
    @Override
    public int hashCode() {
        return Objects.hash(cause, isComplete, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Envelope<?> envelope = (Envelope<?>) o;
        return isComplete == envelope.isComplete &&
            Objects.equals(cause, envelope.cause) &&
            Objects.equals(value, envelope.value);
    }

    public static <T> Envelope<T> complete(Throwable ex) {
        return new Envelope<>(null, ex, true);
    }

    public static <T> Envelope<T> value(T item) {
        return new Envelope<>(item, null, false);
    }
}
