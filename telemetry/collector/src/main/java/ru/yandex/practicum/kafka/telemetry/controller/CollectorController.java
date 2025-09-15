package ru.yandex.practicum.kafka.telemetry.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.kafka.telemetry.model.hub.events.BaseHubEvent;
import ru.yandex.practicum.kafka.telemetry.model.sensors.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.service.handler.EventProcessingService;


@Slf4j
@RestController
@RequestMapping("/events")
public class CollectorController {

    private final EventProcessingService eventProcessingService;
    private final ObjectMapper objectMapper;

    public CollectorController(EventProcessingService eventProcessingService, ObjectMapper objectMapper) {
        this.eventProcessingService = eventProcessingService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/sensors")
    public ResponseEntity<Void> collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        try {
            log.info("Received sensor event: type={}, deviceId={}",
                    event.getType(), event.getHubId());

            log.debug("Full sensor event data: {}", objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize SensorEvent to JSON", e);
        }

        eventProcessingService.processSensorEvent(event);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/hubs")
    public ResponseEntity<Void> collectHubEvent(@Valid @RequestBody BaseHubEvent event) {
        try {
            log.info("Received hub event: type={}, hubId={}",
                    event.getType(), event.getHubId());

            log.debug("Full hub event data: {}", objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize HubEvent to JSON", e);
        }

        eventProcessingService.processHubEvent(event);
        return ResponseEntity.ok().build();
    }
}



/*
@Slf4j
@RestController
@RequestMapping("/events")
public class CollectorController {

    private final EventProcessingService eventProcessingService;
    private final ObjectMapper objectMapper;

    public CollectorController(EventProcessingService eventProcessingService, ObjectMapper objectMapper) {
        this.eventProcessingService = eventProcessingService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/sensors")
    public ResponseEntity<Void> collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        try {
            log.info("Received sensor event: type={}, deviceId={}",
                    event.getType(), event.getHubId());

            log.debug("Full sensor event data: {}", objectMapper.writeValueAsString(event));

            eventProcessingService.processSensorEvent(event);
            return ResponseEntity.ok().build();
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize SensorEvent to JSON", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error processing sensor event", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/hubs")
    public ResponseEntity<Void> collectHubEvent(@Valid @RequestBody BaseHubEvent event) {
        try {
            log.info("Received hub event: type={}, hubId={}",
                    event.getType(), event.getHubId());

            log.debug("Full hub event data: {}", objectMapper.writeValueAsString(event));

            eventProcessingService.processHubEvent(event);
            return ResponseEntity.ok().build();
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize HubEvent to JSON", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error processing hub event", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

 */


















