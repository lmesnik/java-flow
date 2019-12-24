package com.matthisk.flow;

import com.matthisk.flow.operators.FlowOperator;
import java.util.concurrent.ExecutorService;

public interface Flow<T> {
    <U> Flow<U> pipe(FlowOperator<T, U> operator);

    <U, V> Flow<V> pipe(FlowOperator<T, U> op1, FlowOperator<U, V> op2);

    Flow<T> flowOn(ExecutorService executorService);

    void collect(FlowCollector<T> collector);
}
