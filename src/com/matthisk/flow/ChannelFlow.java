package com.matthisk.flow;

import com.matthisk.flow.channels.Channel;
import com.matthisk.flow.channels.Envelope;
import com.matthisk.flow.channels.RendezvousChannel;
import java.util.concurrent.ExecutorService;

public class ChannelFlow<T> extends AbstractFlow<T> {

    private final ExecutorService executor;
    private final Flow<T> inner;
    private final Channel<T> channel;

    ChannelFlow(Flow<T> inner, ExecutorService executor) {
        this.inner = inner;
        this.executor = executor;
        this.channel = new RendezvousChannel<>();
    }

    @Override
    public void collect(FlowCollector<T> collector) {
        executor.submit(() -> {
            try {
                inner.collect(channel::send);
                channel.complete();
            } catch (Throwable ex) {
                channel.complete(ex);
            }
        });

        while (true) {
            Envelope<T> envelope = channel.receive();

            if (envelope.getValue() != null) {
                collector.emit(envelope.getValue());
            }

            if (envelope.getCause() != null) {
                throw new RuntimeException("Received failure from channel", envelope.getCause());
            }

            if (envelope.isComplete()) {
                break;
            }
        }
    }
}
