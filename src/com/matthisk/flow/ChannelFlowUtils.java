package com.matthisk.flow;

import com.matthisk.flow.channels.Channel;
import com.matthisk.flow.channels.Envelope;

public class ChannelFlowUtils {
    static <T> void channelToCollector(Channel<T> channel, FlowCollector<T> collector) {
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
