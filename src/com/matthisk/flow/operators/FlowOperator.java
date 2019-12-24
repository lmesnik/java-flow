package com.matthisk.flow.operators;

import com.matthisk.flow.Flow;

public interface FlowOperator<A, B> {
    Flow<B> apply(Flow<A> inner);

    static <A, B, C> FlowOperator<A, C> compose(FlowOperator<A, B> fn, FlowOperator<B, C> gn) {
        return inner -> gn.apply(fn.apply(inner));
    }
}
