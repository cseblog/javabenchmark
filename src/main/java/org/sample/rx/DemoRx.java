package org.sample.rx;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.sample.model.IEvent;

import java.util.ArrayList;

public class DemoRx {
    public static void main(String[] args) {

        Observable<IEvent> ob1 = ObservableFactory
                .getPriceObservable("Ob1", 10);
        // Producer P2
        Observable<IEvent> ob2 = ObservableFactory
                .getDealObservable("Ob2", 10);

        // Consumer C1
        ob1.mergeWith(ob2).subscribe(event -> {
            System.out.println(event.getData());
        });

    }
}
