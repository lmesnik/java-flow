package com.matthisk.flow;

public class SyncFlow<T> extends AbstractFlow<T> {

    private final FlowProducer<T> producer;

    public SyncFlow(FlowProducer<T> producer) {
        this.producer = producer;
    }

    @Override
    public void collect(FlowCollector<T> collector) {
        producer.accept(collector);
    }

    public static <Y> Flow<Y> flow(FlowProducer<Y> producer) {
        return new SyncFlow<>(producer);
    };
}
