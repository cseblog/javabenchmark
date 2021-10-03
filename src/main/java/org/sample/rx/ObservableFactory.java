package org.sample.rx;

import io.reactivex.rxjava3.core.Observable;
import org.sample.model.DealEvent;
import org.sample.model.IEvent;
import org.sample.model.PriceEvent;

public class ObservableFactory {

    public static Observable<IEvent> getPriceObservable(String name, long n) {
        return Observable.create(emitter -> {
            int loop = (int) n;
            while (loop >= 0) {
                PriceEvent priceEvent = new PriceEvent(String.format("%s: %s - %s", name, Thread.currentThread().getName(), loop));
                emitter.onNext(priceEvent);
                loop--;
            }
        });
    }

    public static Observable<IEvent> getDealObservable(String name, long n) {
        return Observable.create(emitter -> {
            int loop = (int) n;
            while (loop >= 0) {
                DealEvent priceEvent = new DealEvent(String.format("%s: %s - %s", name, Thread.currentThread().getName(), loop));
                emitter.onNext(priceEvent);
                loop--;
            }
        });
    }
}
