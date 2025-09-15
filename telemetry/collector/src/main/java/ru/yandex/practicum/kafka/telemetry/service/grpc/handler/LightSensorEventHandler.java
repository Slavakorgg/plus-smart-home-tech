package ru.yandex.practicum.kafka.telemetry.service.grpc.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Slf4j
@Component
public class LightSensorEventHandler implements SensorEventHandler {

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        try {
            if (!event.hasLightSensorEvent()) {
                log.error("Отсутствуют данные датчика освещенности в событии");
                return;
            }

            LightSensorProto sensor = event.getLightSensorEvent();

            log.info("Обработано событие датчика освещенности:");
            log.info("ID датчика: {}", event.getId());
            log.info("Хаб: {}", event.getHubId());
            log.info("Качество связи: {}", sensor.getLinkQuality());
            log.info("Уровень освещенности: {}", sensor.getLuminosity());

        } catch (Exception e) {
            log.error("Ошибка при обработке события датчика освещенности", e);
        }
    }
}
