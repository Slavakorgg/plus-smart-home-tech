package ru.yandex.practicum.kafka.telemetry.service.handler.hub;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEvent;
import ru.yandex.practicum.kafka.telemetry.model.hub.events.DeviceAddedHubEvent;
import ru.yandex.practicum.kafka.telemetry.model.hub.events.BaseHubEvent;
import ru.yandex.practicum.kafka.telemetry.model.hub.events.HubEventType;
import ru.yandex.practicum.kafka.telemetry.service.EventsSender;

@Component
@AllArgsConstructor
@Slf4j
public class DeviceAddedEventHandler implements HubEventHandler {

    private final EventsSender sender;

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_ADDED;
    }

    @Override
    public void handle(BaseHubEvent hubEvent) {

        log.info("Processing DeviceAddedEvent: type={}, hubId={}",
                hubEvent.getType(), hubEvent.getHubId());

        if (!(hubEvent instanceof DeviceAddedHubEvent deviceAddedHubEvent)) {
            throw new IllegalArgumentException("Expected DeviceAddedEvent");
        }

        log.debug("Detailed DeviceAddedEvent data: {}", hubEvent);

        // Avro-объект для данных датчика
        DeviceAddedEvent payload = new DeviceAddedEvent();
        payload.setId(deviceAddedHubEvent.getId());
        payload.setType(deviceAddedHubEvent.getDeviceType());

        try {

            log.info("Sending DeviceAddedEvent to Kafka: type={}, hubId={}",
                    hubEvent.getType(), hubEvent.getHubId());

            sender.sendHubEvent(deviceAddedHubEvent, payload);

            log.info("DeviceAddedEvent successfully sent to Kafka");
        } catch (Exception e) {
            log.error("Failed to send DeviceAddedEvent to Kafka", e);
        }
    }
}
