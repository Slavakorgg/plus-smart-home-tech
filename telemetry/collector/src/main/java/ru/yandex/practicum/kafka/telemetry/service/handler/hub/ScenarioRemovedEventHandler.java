package ru.yandex.practicum.kafka.telemetry.service.handler.hub;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.model.hub.events.BaseHubEvent;
import ru.yandex.practicum.kafka.telemetry.model.hub.events.HubEventType;
import ru.yandex.practicum.kafka.telemetry.model.hub.events.ScenarioRemovedHubEvent;
import ru.yandex.practicum.kafka.telemetry.service.EventsSender;

@Component
@AllArgsConstructor
@Slf4j
public class ScenarioRemovedEventHandler implements HubEventHandler {

    private final EventsSender sender;

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_REMOVED;
    }

    @Override
    public void handle(BaseHubEvent hubEvent) {

        log.info("Processing ScenarioRemovedEvent: type={}, hubId={}",
                hubEvent.getType(), hubEvent.getHubId());

        if (!(hubEvent instanceof ScenarioRemovedHubEvent scenarioRemovedHubEvent)) {
            throw new IllegalArgumentException("Expected ScenarioAddedHubEvent");
        }

        log.debug("Detailed ScenarioRemovedEvent data: {}", hubEvent);

        // Avro-объект для данных датчика
        ScenarioRemovedEvent payload = new ScenarioRemovedEvent();
        payload.setName(scenarioRemovedHubEvent.getName());

        try {

            log.info("Sending ScenarioRemovedEvent to Kafka: type={}, hubId={}",
                    hubEvent.getType(), hubEvent.getHubId());

            sender.sendHubEvent(scenarioRemovedHubEvent, payload);

            log.info("ScenarioRemovedEvent successfully sent to Kafka");
        } catch (Exception e) {
            log.error("Failed to send ScenarioRemovedEvent to Kafka", e);
        }
    }
}