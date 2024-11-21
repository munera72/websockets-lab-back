package com.udea.websocketslab.controller;

import com.udea.websocketslab.GateInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/gates")
@CrossOrigin(origins = "http://localhost:3000")
public class GateController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
        // private final SimpMessagingTemplate messagingTemplate;

    public GateController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    // Recibir información desde el cliente (React) y enviar a todos los suscriptores
    @MessageMapping("/updateGate")
    @SendTo("/topic/gates")
    public GateInfo updateGateInfo(GateInfo gateInfo) throws Exception
    {
        // Opcionalmente, podrías realizar aquí validaciones o procesos adicionales antes de enviar los datos
        System.out.println("Actualizando información de la puerta: " +
                gateInfo.getGate());

        messagingTemplate.convertAndSend("/topic/gates", gateInfo);


        // Devuelve la información de la puerta a todos los suscriptores
        return new GateInfo(
                HtmlUtils.htmlEscape(gateInfo.getGate()),
                HtmlUtils.htmlEscape(gateInfo.getFlightNumber()),
                HtmlUtils.htmlEscape(gateInfo.getDestination()),
                HtmlUtils.htmlEscape(gateInfo.getDepartureTime()),
                HtmlUtils.htmlEscape(gateInfo.getStatus())
        );
    }



    // Metodo para enviar actualizaciones programáticas (o desde otro servicio)
    public void sendUpdate(GateInfo gateInfo) {
    // Envía los datos actualizados de una puerta de embarque a todos los suscriptores en /topic/gates
        messagingTemplate.convertAndSend("/topic/gates", gateInfo);
    }

    private Map<String, GateInfo> gateData = new ConcurrentHashMap<>();

    // Mtodo para actualizar la información de la puerta de embarque
    @PostMapping("/update")
    public ResponseEntity<String> updateGate(@RequestBody GateInfo gate) {
    // Actualiza los datos de la puerta de embarque
        gateData.put(gate.getGate(), gate);
    // Enviar la actualización a todos los suscriptores WebSocket
        messagingTemplate.convertAndSend("/topic/gates", gate);
        return ResponseEntity.ok("Puerta de embarque actualizada correctamente");
    }
    @GetMapping("/{gateNumber}")
    public ResponseEntity<GateInfo> getGateInfo(@PathVariable String
                                                        gateNumber) {
        GateInfo gate = gateData.get(gateNumber);
        return ResponseEntity.ok(gate);
    }
}