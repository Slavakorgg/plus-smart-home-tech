package ru.yandex.practicum.kafka.telemetry.service.handler.sensors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.model.sensors.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.model.sensors.SensorEventType;
import ru.yandex.practicum.kafka.telemetry.model.sensors.SwitchSensorEvent;
import ru.yandex.practicum.kafka.telemetry.service.EventsSender;

@Component
@AllArgsConstructor
@Slf4j
public class SwitchSensorEventHandler implements SensorEventHandler {

    private final EventsSender sender;

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEvent sensorEvent) {

        log.info("Processing SwitchSensorEvent: type={}, hubId={}",
                sensorEvent.getType(), sensorEvent.getHubId());

        if (!(sensorEvent instanceof SwitchSensorEvent switchSensorEvent)) {
            throw new IllegalArgumentException("Expected SwitchSensorEvent");
        }

        log.debug("Detailed SwitchSensorEvent data: {}", sensorEvent);

        // Avro-объект для данных датчика
        SwitchSensorAvro payload = new SwitchSensorAvro();
        payload.setState(switchSensorEvent.getState());

        try {

            log.info("Sending SwitchSensorEvent to Kafka: type={}, hubId={}",
                    sensorEvent.getType(), sensorEvent.getHubId());

            sender.sendSensorEvent(switchSensorEvent, payload);

            log.info("SwitchSensorEvent successfully sent to Kafka");
        } catch (Exception e) {
            log.error("Failed to send SwitchSensorEvent to Kafka", e);
        }
    }
}



