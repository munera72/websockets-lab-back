package com.udea.websocketslab;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightInfo {

    private String latitude;
    private String longitude;
    private String heading;
    private String speed;
    private String flightCode;
    private String altitude;


}
