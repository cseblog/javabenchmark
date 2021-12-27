package org.sample.disruptor;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.sample.model.IEvent;
import org.sample.model.PriceEvent;

public class DisruptorFactory {

    public static Disruptor<IEvent> factory() {
        int bufferSize = 1024;
        Disruptor<IEvent> disruptor = new Disruptor(new LongEventFactory(),
                bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.MULTI, new YieldingWaitStrategy());
        return disruptor;
    }

    public static class LongEventFactory implements EventFactory<IEvent> {
        @Override
        public IEvent newInstance() {
            return new PriceEvent();
        }
    }
}