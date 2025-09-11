package ru.yandex.practicum.kafka.telemetry.service.handler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.model.hub.events.HubEventType;
import ru.yandex.practicum.kafka.telemetry.model.sensors.SensorEventType;
import ru.yandex.practicum.kafka.telemetry.service.handler.hub.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.service.handler.sensors.SensorEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class EventProcessingConfig {

    @Bean
    public EventProcessingService eventProcessingService(
            Set<HubEventHandler> hubEventHandlers,
            Set<SensorEventHandler> sensorEventHandlers) {

        Map<HubEventType, HubEventHandler> hubMap = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));

        Map<SensorEventType, SensorEventHandler> sensorMap = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getMessageType, Function.identity()));

        return new EventProcessingServiceImpl(hubMap, sensorMap);
    }
}




