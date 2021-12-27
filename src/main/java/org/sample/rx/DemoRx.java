package org.sample.rx;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.sample.model.IEvent;

import java.util.ArrayList;

public class DemoRx {
    public static void main(String[] args) throws InterruptedException {

        Observable<IEvent> ob1 = ObservableFactory
                .getPriceObservable("Ob1", 10);
        // Producer P2
        Observable<IEvent> ob2 = ObservableFactory
                .getDealObservable("Ob2", 10);

        // Consumer C1
        ob1.subscribeOn(Schedulers.newThread()).mergeWith(ob2.subscribeOn(Schedulers.newThread()))
                .subscribeOn(Schedulers.newThread())
                .subscribe(event -> {
            System.out.println(event.getData());
        });

        //Waiting for both T1, and T2 finished
        while(ObservableFactory.isBothComplete != 2){
        }
    }
}
