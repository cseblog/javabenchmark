package org.sample.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Data
@NoArgsConstructor
public class DealEvent implements IEvent {
    private String data;
}
