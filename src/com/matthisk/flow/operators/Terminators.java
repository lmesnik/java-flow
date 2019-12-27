package com.matthisk.flow.operators;

import java.util.ArrayList;
import java.util.List;

public class Terminators {
    public static <T> FlowTerminator<T, List<T>> toList() {
        return flow -> {
            ArrayList<T> result = new ArrayList<>();
            flow.collect(result::add);
            return result;
        };
    }
}
