package org.sample.rx;

import io.reactivex.rxjava3.core.Observable;
import org.sample.model.IEvent;
import org.sample.model.PriceEvent;

public class ObservableFactory {

    public static volatile int isBothComplete = 0;

    public static Observable<IEvent> getPriceObservable(String name, long n) {
        return Observable.create(emitter -> {
            int i = 0;
            while (i < n) {
                PriceEvent priceEvent = new PriceEvent(String.format("%s: %s - %s", name, Thread.currentThread().getName(), i));
                emitter.onNext(priceEvent);
                i++;
            }
            isBothComplete += 1;
            emitter.onComplete();

        });
    }

    public static Observable<IEvent> getDealObservable(String name, long n) {

        return Observable.create(emitter -> {
            int i = 0;
            while (i < n) {
                PriceEvent priceEvent = new PriceEvent(String.format("%s: %s - %s", name, Thread.currentThread().getName(), i));
                emitter.onNext(priceEvent);
                i++;
            }
            isBothComplete += 1;
            emitter.onComplete();
        });
    }
}
