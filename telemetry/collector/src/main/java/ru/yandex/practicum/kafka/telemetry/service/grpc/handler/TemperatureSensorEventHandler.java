package ru.yandex.practicum.kafka.telemetry.service.grpc.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;

@Slf4j
@Component
public class TemperatureSensorEventHandler implements SensorEventHandler {

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        try {
            if (!event.hasTemperatureSensorEvent()) {
                log.error("Отсутствуют данные температурного датчика в событии");
                return;
            }

            TemperatureSensorProto sensor = event.getTemperatureSensorEvent();

            log.info("Обработано событие температурного датчика:");
            log.info("ID датчика: {}", event.getId());
            log.info("Хаб: {}", event.getHubId());
            log.info("Температура (C): {}", sensor.getTemperatureC());
            log.info("Температура (F): {}", sensor.getTemperatureF());

        } catch (Exception e) {
            log.error("Ошибка при обработке события температурного датчика", e);
        }
    }
}
