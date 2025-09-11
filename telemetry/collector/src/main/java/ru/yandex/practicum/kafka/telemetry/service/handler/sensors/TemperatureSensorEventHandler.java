package ru.yandex.practicum.kafka.telemetry.service.handler.sensors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.kafka.telemetry.model.sensors.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.model.sensors.SensorEventType;
import ru.yandex.practicum.kafka.telemetry.model.sensors.TemperatureSensorEvent;
import ru.yandex.practicum.kafka.telemetry.service.EventsSender;

@Component
@AllArgsConstructor
@Slf4j
public class TemperatureSensorEventHandler implements SensorEventHandler {

    private final EventsSender sender;

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEvent sensorEvent) {

        log.info("Processing TemperatureSensorEvent: type={}, hubId={}",
                sensorEvent.getType(), sensorEvent.getHubId());

        if (!(sensorEvent instanceof TemperatureSensorEvent temperatureSensorEvent)) {
            throw new IllegalArgumentException("Expected TemperatureSensorEvent");
        }

        log.debug("Detailed TemperatureSensorEvent data: {}", sensorEvent);

        // Avro-объект для данных датчика
        TemperatureSensorAvro payload = new TemperatureSensorAvro();
        payload.setTemperatureC(temperatureSensorEvent.getTemperatureC());
        payload.setTemperatureF(temperatureSensorEvent.getTemperatureF());

        try {

            log.info("Sending TemperatureSensorEvent to Kafka: type={}, hubId={}",
                    sensorEvent.getType(), sensorEvent.getHubId());

            sender.sendSensorEvent(temperatureSensorEvent, payload);

            log.info("TemperatureSensorEvent successfully sent to Kafka");
        } catch (Exception e) {
            log.error("Failed to send TemperatureSensorEvent to Kafka", e);
        }
    }
}
