package com.matthisk.flow.channels;

public interface SendChannel<T> {
    void send(T item);

    void complete(Throwable ex);

    void complete();
}
