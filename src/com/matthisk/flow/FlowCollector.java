package com.matthisk.flow;

public interface FlowCollector<T> {
    void emit(T value);
}
