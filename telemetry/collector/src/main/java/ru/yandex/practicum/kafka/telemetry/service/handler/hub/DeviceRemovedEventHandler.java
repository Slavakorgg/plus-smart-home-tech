package ru.yandex.practicum.kafka.telemetry.service.handler.hub;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.model.hub.events.BaseHubEvent;
import ru.yandex.practicum.kafka.telemetry.model.hub.events.DeviceRemovedHubEvent;
import ru.yandex.practicum.kafka.telemetry.model.hub.events.HubEventType;
import ru.yandex.practicum.kafka.telemetry.service.EventsSender;

@Component
@AllArgsConstructor
@Slf4j
public class DeviceRemovedEventHandler implements HubEventHandler {

    private final EventsSender sender;

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_REMOVED;
    }

    @Override
    public void handle(BaseHubEvent hubEvent) {
        log.info("Processing DeviceRemovedEvent: type={}, hubId={}",
                hubEvent.getType(), hubEvent.getHubId());
        if (!(hubEvent instanceof DeviceRemovedHubEvent deviceRemovedHubEvent)) {
            throw new IllegalArgumentException("Expected DeviceRemovedHubEvent");

        }

        log.debug("Detailed DeviceRemovedEvent data: {}", hubEvent);

        DeviceRemovedHubEvent payload = new DeviceRemovedHubEvent();
        payload.setId(deviceRemovedHubEvent.getId());

        try {

            log.info("Sending DeviceRemovedEvent to Kafka: type={}, hubId={}",
                    hubEvent.getType(), hubEvent.getHubId());

            sender.sendHubEvent(deviceRemovedHubEvent, payload);

            log.info("DeviceRemovedEvent successfully sent to Kafka");
        } catch (Exception e) {
            log.error("Failed to send DeviceRemovedEvent to Kafka", e);
        }
    }
}