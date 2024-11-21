package com.udea.websocketslab;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GateInfo {

    private String gate;
    private String flightNumber;
    private String destination;
    private String departureTime;
    private String status;

}
