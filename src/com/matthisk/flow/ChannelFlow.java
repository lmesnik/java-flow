package com.matthisk.flow;

import static com.matthisk.flow.ChannelFlowUtils.channelToCollector;

import com.matthisk.flow.channels.Channel;
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

        channelToCollector(channel, collector);
    }
}
