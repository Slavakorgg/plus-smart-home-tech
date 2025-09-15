package ru.yandex.practicum.kafka.telemetry.service.grpc.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Slf4j
@Component
public class ClimateSensorEventGrpcHandler implements SensorEventGrpcHandler {

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        try {
            if (!event.hasClimateSensorEvent()) {
                log.error("Отсутствуют данные климатического датчика в событии");
                return;
            }

            ClimateSensorProto sensor = event.getClimateSensorEvent();

            log.info("Обработано событие климатического датчика:");
            log.info("ID датчика: {}", event.getId());
            log.info("Хаб: {}", event.getHubId());
            log.info("Температура (C): {}", sensor.getTemperatureC());
            log.info("Влажность: {}", sensor.getHumidity());
            log.info("Уровень CO2: {}", sensor.getCo2Level());


        } catch (Exception e) {
            log.error("Ошибка при обработке события климатического датчика", e);
        }
    }
}