package com.matthisk.flow.channels;

public interface ReceiveChannel<T> {
    Envelope<T> receive();
}
