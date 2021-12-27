package org.sample;

import com.lmax.disruptor.dsl.Disruptor;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.sample.disruptor.DisruptorFactory;
import org.sample.model.IEvent;
import org.sample.model.ProducerThread;
import org.sample.rx.ObservableFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(value = 1, warmups = 1)
public class StreamDisruptorBenchmark {

    @Benchmark
    public void testReactiveMethod(Blackhole blackhole, BenchmarkState state) throws InterruptedException {
        // Producer P1
        Observable<IEvent> ob1 = ObservableFactory
                .getPriceObservable("Ob1", state.numberMsgTotal);

        // Producer P2
        Observable<IEvent> ob2 = ObservableFactory
                .getDealObservable("Ob2", state.numberMsgTotal);

        // Consumer C1
        ob1.mergeWith(ob2).subscribe(event -> {
            state.resultArray.add(event.getData());
        });
        blackhole.consume(state.resultArray);
    }

    @Benchmark
    public void testDisruptorMethod(Blackhole blackhole, BenchmarkState state) throws InterruptedException {
        state.t1.setLoop(state.numberMsgTotal);
        state.t2.setLoop(state.numberMsgTotal);

        // Starting P1, P2 producers
        state.t1.start();
        state.t2.start();

        state.t1.join();
        state.t2.join();

        //Throw the result into black hole
        blackhole.consume(state.resultArray);
    }


    @State(Scope.Thread)
    public static class BenchmarkState {
        @Param({"10000", "100000", "1000000"})
        public int numberMsgTotal;
        public Disruptor<IEvent> disruptor;
        public ProducerThread t1, t2;
        public List<String> resultArray;

        @Setup(Level.Iteration)
        public void setup() {
            resultArray = new ArrayList<>();
            disruptor = DisruptorFactory.factory();

            // Set up event consumer
            disruptor.handleEventsWith((event, l, b) -> {
                resultArray.add(event.getData());
            });

            // Set up event
            disruptor.start();
            t1 = new ProducerThread("T1", disruptor.getRingBuffer(), numberMsgTotal);
            t2 = new ProducerThread("T2", disruptor.getRingBuffer(), numberMsgTotal);
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            disruptor.shutdown();
            resultArray.clear();
        }
    }
}
