package com.matthisk.flow;

public interface FlowProducer<T> {
    void accept(FlowCollector<T> collector);
}
