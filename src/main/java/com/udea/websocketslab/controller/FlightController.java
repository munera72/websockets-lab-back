package com.udea.websocketslab.controller;

import com.udea.websocketslab.FlightInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "http://localhost:3000")
public class FlightController {

    @Autowired
    private SimpMessagingTemplate template;

    public FlightController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/updateFlight")
    @SendTo("/topic/flights")
    public FlightInfo updateFlightInfo(FlightInfo flightInfo) throws Exception {

        System.out.println("Actualizando información del vuelo: " +
                flightInfo.getFlightCode());

        System.out.println(fligthData);

        template.convertAndSend("/topic/flights", flightInfo);

        return new FlightInfo(
                HtmlUtils.htmlEscape(flightInfo.getLatitude()),
                HtmlUtils.htmlEscape(flightInfo.getLongitude()),
                HtmlUtils.htmlEscape(flightInfo.getHeading()),
                HtmlUtils.htmlEscape(flightInfo.getSpeed()),
                HtmlUtils.htmlEscape(flightInfo.getFlightCode()),
                HtmlUtils.htmlEscape(flightInfo.getAltitude())
        );

    }

    // Metodo para enviar actualizaciones programáticas (o desde otro servicio)
    public void sendUpdate(FlightInfo flightInfo) {
        // Envía los datos actualizados de una puerta de embarque a todos los suscriptores en /topic/gates
        template.convertAndSend("/topic/flights", flightInfo);
    }

    private Map<String, FlightInfo> fligthData = new ConcurrentHashMap<>();

    @PostMapping("/update")
    public ResponseEntity<String> updateFlight(@RequestBody FlightInfo flight) {
        fligthData.put(flight.getFlightCode(), flight);

        template.convertAndSend("/topic/flights", flight);
        return ResponseEntity.ok("Vuelo actualizado exitosamente");
    }

    @GetMapping("/{flightCode}")
    public ResponseEntity<FlightInfo> getFlightInfo(@PathVariable String flightCode) {
        FlightInfo flight = fligthData.get(flightCode);
        return ResponseEntity.ok(flight);
    }



}
