package org.sample;

import com.lmax.disruptor.dsl.Disruptor;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.sample.disruptor.DisruptorFactory;
import org.sample.disruptor.ThreadProducer;
import org.sample.model.IEvent;
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
        ObservableFactory.isBothComplete = 0;
        Observable<IEvent> ob1 = ObservableFactory
                .getPriceObservable("T1", state.numberMsgTotal);
        // Producer P2
        Observable<IEvent> ob2 = ObservableFactory
                .getDealObservable("T2", state.numberMsgTotal);

        // Consumer C1
        ob1.subscribeOn(Schedulers.newThread()).mergeWith(ob2.subscribeOn(Schedulers.newThread()))
                .subscribe(event -> {
                    state.resultArray.add(event.getData());
                });

        //Waiting for both T1, and T2 finished
        while(ObservableFactory.isBothComplete != 2){
        }
        blackhole.consume(state.resultArray);
    }

    @Benchmark
    public void testDisruptorMethod(Blackhole blackhole, BenchmarkState state) throws InterruptedException {

        ThreadProducer t1 = new ThreadProducer("T1", state.disruptor.getRingBuffer(), state.numberMsgTotal);
        ThreadProducer t2 = new ThreadProducer("T2", state. disruptor.getRingBuffer(), state.numberMsgTotal);
        // Starting P1, P2 producers
        t1.start();
        t2.start();

        //Waiting for t1, and t2 finished
        t1.join();
        t2.join();

        //Throw the result into black hole
        blackhole.consume(state.resultArray);
    }


    @State(Scope.Benchmark)
    public static class BenchmarkState {
        @Param({"10000", "100000", "1000000"})
        public int numberMsgTotal;

        public Disruptor<IEvent> disruptor;
        public List<String> resultArray;

        @Setup(Level.Invocation)
        public void setup() {
            resultArray = new ArrayList<>();
            disruptor = DisruptorFactory.factory();

            // Set up event consumer
            disruptor.handleEventsWith((event, l, b) -> {
                resultArray.add(event.getData());
            });

            // Set up event
            disruptor.start();
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            disruptor.shutdown();
            resultArray.clear();

        }
    }
}
