package com.matthisk.flow.channels;

public interface Channel<T> extends ReceiveChannel<T>, SendChannel<T> {
}
