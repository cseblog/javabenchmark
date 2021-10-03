package org.sample.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Data
@NoArgsConstructor
public class PriceEvent implements IEvent {
    private String data;
}
