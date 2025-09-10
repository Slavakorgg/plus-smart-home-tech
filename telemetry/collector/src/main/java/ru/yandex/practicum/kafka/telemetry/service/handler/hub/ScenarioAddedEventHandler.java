package ru.yandex.practicum.kafka.telemetry.service.handler.hub;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEvent;
import ru.yandex.practicum.kafka.telemetry.model.hub.events.BaseHubEvent;
import ru.yandex.practicum.kafka.telemetry.model.hub.events.HubEventType;
import ru.yandex.practicum.kafka.telemetry.model.hub.events.ScenarioAddedHubEvent;
import ru.yandex.practicum.kafka.telemetry.service.EventsSender;

@Component
@AllArgsConstructor
@Slf4j
public class ScenarioAddedEventHandler implements HubEventHandler {

    private final EventsSender sender;

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }

    @Override
    public void handle(BaseHubEvent hubEvent) {

        log.info("Processing ScenarioAddedEvent: type={}, hubId={}",
                hubEvent.getType(), hubEvent.getHubId());

        if (!(hubEvent instanceof ScenarioAddedHubEvent scenarioAddedHubEvent)) {
            throw new IllegalArgumentException("Expected ScenarioAddedHubEvent");
        }

        log.debug("Detailed ScenarioAddedEvent data: {}", hubEvent);

        // Avro-объект для данных датчика
        ScenarioAddedEvent payload = new ScenarioAddedEvent();
        payload.setName(scenarioAddedHubEvent.getName());
        payload.setConditions(scenarioAddedHubEvent.getConditions());
        payload.setActions(scenarioAddedHubEvent.getActions());

        try {

            log.info("Sending ScenarioAddedEvent to Kafka: type={}, hubId={}",
                    hubEvent.getType(), hubEvent.getHubId());

            sender.sendHubEvent(scenarioAddedHubEvent, payload);

            log.info("ScenarioAddedEvent successfully sent to Kafka");
        } catch (Exception e) {
            log.error("Failed to send ScenarioAddedEvent to Kafka", e);
        }
    }
}