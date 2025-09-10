package ru.yandex.practicum.kafka.telemetry.service.handler.sensors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.model.sensors.ClimateSensorEvent;
import ru.yandex.practicum.kafka.telemetry.model.sensors.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.model.sensors.SensorEventType;
import ru.yandex.practicum.kafka.telemetry.service.EventsSender;

@Component
@AllArgsConstructor
@Slf4j
public class ClimateSensorEventHandler implements SensorEventHandler {

    private final EventsSender sender;

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEvent sensorEvent) {

        log.info("Processing ClimateSensorEvent: type={}, hubId={}",
                sensorEvent.getType(), sensorEvent.getHubId());

        if (!(sensorEvent instanceof ClimateSensorEvent climateSensorEvent)) {
            throw new IllegalArgumentException("Expected ClimateSensorEvent");
        }

        log.debug("Detailed ClimateSensorEvent data: {}", sensorEvent);

        // Avro-объект для данных датчика
        ClimateSensorAvro payload = new ClimateSensorAvro();
        payload.setTemperatureC(climateSensorEvent.getTemperatureC());
        payload.setHumidity(climateSensorEvent.getHumidity());
        payload.setCo2Level(climateSensorEvent.getCo2Level());

        try {

            log.info("Sending ClimateSensorEvent to Kafka: type={}, hubId={}",
                    sensorEvent.getType(), sensorEvent.getHubId());

            sender.sendSensorEvent(climateSensorEvent, payload);


            log.info("ClimateSensorEvent successfully sent to Kafka");
        } catch (Exception e) {
            log.error("Failed to send ClimateSensorEvent to Kafka", e);
        }
    }
}