package com.matthisk.flow;

import com.matthisk.flow.operators.FlowOperator;
import com.matthisk.flow.operators.FlowTerminator;
import java.util.concurrent.ExecutorService;

public abstract class AbstractFlow<T> implements Flow<T> {

    @Override
    public Flow<T> flowOn(ExecutorService executor) {
        return new ChannelFlow<>(this, executor);
    }

    @Override
    public <Y> Flow<Y> pipe(FlowOperator<T, Y> operator) {
        return operator.apply(this);
    }

    @Override
    public <Q, R> Flow<R> pipe(FlowOperator<T, Q> op1, FlowOperator<Q, R> op2) {
        return FlowOperator.compose(op1, op2).apply(this);
    }

    @Override
    public <U> U terminate(FlowTerminator<T, U> terminator) {
        return terminator.apply(this);
    }
}
