package org.sample.rx;

import io.reactivex.rxjava3.core.Observable;
import org.sample.model.DealEvent;
import org.sample.model.IEvent;
import org.sample.model.PriceEvent;

public class ObservableFactory {

    public static Observable<IEvent> getPriceObservable(String name, long n) {
        return Observable.create(emitter -> {

            Thread t = new Thread(){
                @Override
                public void run(){
                    int i = (int) n;
                    while (i >= 0) {
                        PriceEvent priceEvent = new PriceEvent(String.format("%s: %s - %s", name, Thread.currentThread().getName(), i));
                        emitter.onNext(priceEvent);
                        i--;
                    }
                }
            };
            t.start();

        });
    }

    public static Observable<IEvent> getDealObservable(String name, long n) {
        return Observable.create(emitter -> {
            Thread t = new Thread() {
                @Override
                public void run() {
                    int i = (int) n;
                    while (i >= 0) {
                        DealEvent priceEvent = new DealEvent(String.format("%s: %s - %s", name, Thread.currentThread().getName(), i));
                        emitter.onNext(priceEvent);
                        i--;
                    }
                }
            };
            t.start();
        });
    }
}
