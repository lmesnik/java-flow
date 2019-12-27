package com.matthisk.flow.operators;

import com.matthisk.flow.ChannelMergeFlow;
import com.matthisk.flow.Flow;
import com.matthisk.flow.SyncFlow;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.Predicate;

public class Operators {
    public static <A, B> FlowOperator<A, B> map(Function<A, B> fn) {
        return inner -> SyncFlow.flow(producer -> inner.collect(value -> producer.emit(fn.apply(value))));
    }

    public static <A> FlowOperator<A, A> filter(Predicate<A> predicate) {
        return inner ->
                SyncFlow.flow(
                        collector ->
                                inner.collect(
                                        value -> {
                                            if (predicate.test(value)) collector.emit(value);
                                        }));
    }

    public static <A, B> FlowOperator<A, B> flatMap(Function<A, Flow<B>> fn) {
        return FlowOperator.compose(map(fn), flattenConcat());
    }

    public static <A> FlowOperator<Flow<A>, A> flattenConcat() {
        return inner -> SyncFlow.flow(collector -> inner.collect(value  -> value.collect(collector)));
    }

    public static <A> FlowOperator<Flow<A>, A> flattenMerge(
            int concurrency, ExecutorService executorService) {
        return inner -> new ChannelMergeFlow<A>(inner, concurrency, executorService);
    }
}
