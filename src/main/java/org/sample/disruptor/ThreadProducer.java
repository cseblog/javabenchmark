package org.sample.disruptor;

import com.lmax.disruptor.RingBuffer;
import lombok.SneakyThrows;
import org.sample.model.IEvent;

public class ThreadProducer extends Thread {
    int loop;
    RingBuffer<IEvent> ringBuffer;
    String name;

    public ThreadProducer(String name, RingBuffer<IEvent> ringBuffer, int loop) {
        this.ringBuffer = ringBuffer;
        this.name = name;
        this.loop = loop;
    }

    public void setLoop(int loop) {
        this.loop = loop;
    }

    @SneakyThrows
    @Override
    public void run() {
        while (loop >= 0) {
            long nextIndex = ringBuffer.next();
            try {
                IEvent event = ringBuffer.get(nextIndex);
                String s = String.format("%s: %s - %s", name, Thread.currentThread().getName(), loop);
                event.setData(s);
            } finally {
                ringBuffer.publish(nextIndex);
            }
            loop--;
        }
    }
}
