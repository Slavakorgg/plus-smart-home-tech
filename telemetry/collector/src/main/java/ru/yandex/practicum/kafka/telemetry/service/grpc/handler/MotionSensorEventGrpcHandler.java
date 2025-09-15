package ru.yandex.practicum.kafka.telemetry.service.grpc.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Slf4j
@Component
public class MotionSensorEventGrpcHandler implements SensorEventGrpcHandler {

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        try {
            if (!event.hasMotionSensorEvent()) {
                log.error("Отсутствует данные датчика движения в событии");
                return;
            }

            MotionSensorProto sensor = event.getMotionSensorEvent();

            log.info("Обработано событие датчика движения:");
            log.info("ID датчика: {}", event.getId());
            log.info("Хаб: {}", event.getHubId());
            log.info("Качество связи: {}", sensor.getLinkQuality());
            log.info("Движение обнаружено: {}", sensor.getMotion());
            log.info("Напряжение: {}", sensor.getVoltage());

        } catch (Exception e) {
            log.error("Ошибка при обработке события датчика движения", e);
        }
    }
}
