package ru.yandex.practicum.kafka.telemetry.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.model.hub.events.BaseHubEvent;
import ru.yandex.practicum.kafka.telemetry.model.hub.events.HubEventType;
import ru.yandex.practicum.kafka.telemetry.model.sensors.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.model.sensors.SensorEventType;
import ru.yandex.practicum.kafka.telemetry.service.handler.hub.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.service.handler.sensors.SensorEventHandler;

import java.util.Map;


@Service
@Slf4j
public class EventProcessingServiceImpl implements EventProcessingService {

    private final Map<HubEventType, HubEventHandler> hubEventHandlers;
    private final Map<SensorEventType, SensorEventHandler> sensorEventHandlers;

    public EventProcessingServiceImpl(
            Map<HubEventType, HubEventHandler> hubEventHandlers,
            Map<SensorEventType, SensorEventHandler> sensorEventHandlers) {
        this.hubEventHandlers = hubEventHandlers;
        this.sensorEventHandlers = sensorEventHandlers;
    }

    @Override
    public void processSensorEvent(SensorEvent event) {
        if (sensorEventHandlers.containsKey(event.getType())) {
            sensorEventHandlers.get(event.getType()).handle(event);
        } else {
            throw new IllegalArgumentException("Can't find handler for " + event.getType());
        }
    }

    @Override
    public void processHubEvent(BaseHubEvent event) {
        if (hubEventHandlers.containsKey(event.getType())) {
            hubEventHandlers.get(event.getType()).handle(event);
        } else {
            throw new IllegalArgumentException("Can't find handler for " + event.getType());
        }
    }
}













