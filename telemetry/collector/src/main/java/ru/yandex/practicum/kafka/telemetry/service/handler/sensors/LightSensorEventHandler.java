package ru.yandex.practicum.kafka.telemetry.service.handler.sensors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.model.sensors.LightSensorEvent;
import ru.yandex.practicum.kafka.telemetry.model.sensors.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.model.sensors.SensorEventType;
import ru.yandex.practicum.kafka.telemetry.service.EventsSender;

@Component
@AllArgsConstructor
@Slf4j
public class LightSensorEventHandler implements SensorEventHandler {

    private final EventsSender sender;


    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEvent sensorEvent) {

        log.info("Processing LightSensorEvent: type={}, hubId={}",
                sensorEvent.getType(), sensorEvent.getHubId());

        if (!(sensorEvent instanceof LightSensorEvent lightEvent)) {
            throw new IllegalArgumentException("Expected LightSensorEvent");
        }

        log.debug("Detailed LightSensorEvent data: {}", sensorEvent);

        // Avro-объект для данных датчика
        LightSensorAvro payload = new LightSensorAvro();
        payload.setLinkQuality(lightEvent.getLinkQuality());
        payload.setLuminosity(lightEvent.getLuminosity());

        try {

            log.info("Sending LightSensorEvent to Kafka: type={}, hubId={}",
                    sensorEvent.getType(), sensorEvent.getHubId());

            sender.sendSensorEvent(lightEvent, payload);

            log.info("LightSensorEvent successfully sent to Kafka");
        } catch (Exception e) {
            log.error("Failed to send LightSensorEvent to Kafka", e);
        }
    }
}
