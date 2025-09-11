package ru.yandex.practicum.kafka.telemetry.service.handler.sensors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.model.sensors.MotionSensorEvent;
import ru.yandex.practicum.kafka.telemetry.model.sensors.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.model.sensors.SensorEventType;
import ru.yandex.practicum.kafka.telemetry.service.EventsSender;

@Component
@AllArgsConstructor
@Slf4j
public class MotionSensorEventHandler implements SensorEventHandler {

    private final EventsSender sender;

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEvent sensorEvent) {

        log.info("Processing MotionSensorEvent: type={}, hubId={}",
                sensorEvent.getType(), sensorEvent.getHubId());

        if (!(sensorEvent instanceof MotionSensorEvent motionSensorEvent)) {
            throw new IllegalArgumentException("Expected MotionSensorEvent");
        }

        log.debug("Detailed MotionSensorEvent data: {}", sensorEvent);

        // Avro-объект для данных датчика
        MotionSensorAvro payload = new MotionSensorAvro();
        payload.setLinkQuality(motionSensorEvent.getLinkQuality());
        payload.setMotion(motionSensorEvent.getMotion());
        payload.setVoltage(motionSensorEvent.getVoltage());

        try {

            log.info("Sending MotionSensorEvent to Kafka: type={}, hubId={}",
                    sensorEvent.getType(), sensorEvent.getHubId());

            sender.sendSensorEvent(motionSensorEvent, payload);

            log.info("MotionSensorEvent successfully sent to Kafka");
        } catch (Exception e) {
            log.error("Failed to send MotionSensorEvent to Kafka", e);
        }
    }
}