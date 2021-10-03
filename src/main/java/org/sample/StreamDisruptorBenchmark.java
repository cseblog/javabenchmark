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
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 1, warmups = 1)
public class StreamDisruptorBenchmark {

    @Benchmark
    public List<String> testReactiveMethod(Blackhole blackhole, BenchmarkState state) throws InterruptedException {
        List<String> array = new ArrayList<>();
        Observable<IEvent> ob1 = ObservableFactory
                .getPriceObservable("Ob1", state.loop)
                .subscribeOn(Schedulers.newThread());

        Observable<IEvent> ob2 = ObservableFactory
                .getDealObservable("Ob2", state.loop)
                .subscribeOn(Schedulers.newThread());

        ob1.mergeWith(ob2).subscribe(event -> {
            array.add(event.getData());
        });
        return array;
    }

    @Benchmark
    public List<String> testDisruptorMethod(Blackhole blackhole, BenchmarkState state) throws InterruptedException {
        state.t1.setLoop(state.loop);
        state.t2.setLoop(state.loop);

        //Starting two producers
        state.t1.start();
        state.t2.start();


        state.t1.join();
        state.t2.join();
        return state.result;
    }


    @State(Scope.Thread)
    public static class BenchmarkState {
        @Param({"10000", "100000", "1000000"})
        public int loop;
        public Disruptor<IEvent> disruptor;
        public ProducerThread t1;
        public ProducerThread t2;

        public List<String> result = new ArrayList<>();

        @Setup(Level.Invocation)
        public void setup() {
            disruptor = DisruptorFactory.factory();
            disruptor.handleEventsWith((event, l, b) -> {
                result.add(event.getData());
            });

            disruptor.start();
            t1 = new ProducerThread("T1", disruptor.getRingBuffer(), loop);
            t2 = new ProducerThread("T2", disruptor.getRingBuffer(), loop);
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            disruptor.shutdown();
            result.clear();
        }
    }


}
