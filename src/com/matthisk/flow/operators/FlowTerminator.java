package com.matthisk.flow.operators;

import com.matthisk.flow.Flow;

public interface FlowTerminator<T, U> {
    U apply(Flow<T> flow);
}
