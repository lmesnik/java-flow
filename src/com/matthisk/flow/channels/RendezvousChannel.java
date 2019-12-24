package com.matthisk.flow.channels;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RendezvousChannel<T> implements Channel<T> {

    private final BlockingQueue<Envelope<T>> queue;

    public RendezvousChannel() {
        this.queue = new ArrayBlockingQueue<>(1);
    }

    @Override
    public Envelope<T> receive() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while receiving from channel", e);
        }
    }

    @Override
    public void send(T item) {
        try {
            queue.put(Envelope.value(item));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while sending on channel", e);
        }
    }

    @Override
    public void complete(Throwable ex) {
        try {
            queue.put(Envelope.complete(ex));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while closing channel", e);
        }
    }

    @Override
    public void complete() {
        try {
            queue.put(Envelope.complete(null));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while closing channel", e);
        }
    }
}
