package org.sample.disruptor;

import lombok.Getter;
import lombok.Setter;
import org.openjdk.jmh.infra.Blackhole;

@Getter
@Setter
public class EventProcessor {

    public static <PriceEvent> void rateProcess(PriceEvent event) {

    }
    public static <DealEvent> void dealProcess(DealEvent event) {

    }

    public static <String> void eventProcess(String event){
        System.out.println(event);
    }

}
