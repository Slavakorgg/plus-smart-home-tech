package ru.yandex.practicum.kafka.telemetry.service.handler;

import ru.yandex.practicum.kafka.telemetry.model.hub.events.BaseHubEvent;
import ru.yandex.practicum.kafka.telemetry.model.sensors.SensorEvent;

public interface EventProcessingService {
    void processSensorEvent(SensorEvent event);
    void processHubEvent(BaseHubEvent event);
}
