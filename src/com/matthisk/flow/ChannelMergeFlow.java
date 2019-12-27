package com.matthisk.flow;

import static com.matthisk.flow.ChannelFlowUtils.channelToCollector;

import com.matthisk.flow.channels.Channel;
import com.matthisk.flow.channels.RendezvousChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

public class ChannelMergeFlow<T> extends AbstractFlow<T> {

    private final int concurrency;
    private final ExecutorService executor;
    private final Flow<Flow<T>> inner;
    private final Channel<T> channel;

    public ChannelMergeFlow(Flow<Flow<T>> inner, int concurrency, ExecutorService executor) {
        this.inner = inner;
        this.concurrency = concurrency;
        this.executor = executor;
        this.channel = new RendezvousChannel<T>();
    }

    @Override
    public void collect(FlowCollector<T> collector) {
        Semaphore semaphore = new Semaphore(concurrency);

        inner.collect(flow -> {
            executor.submit(() -> {
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Unable to acquire lock", e);
                }

                try {
                    flow.collect(channel::send);
                    channel.complete();
                } catch (Throwable ex) {
                    channel.complete(ex);
                } finally {
                    semaphore.release();
                }
            });
        });

        channelToCollector(channel, collector);
    }
}
