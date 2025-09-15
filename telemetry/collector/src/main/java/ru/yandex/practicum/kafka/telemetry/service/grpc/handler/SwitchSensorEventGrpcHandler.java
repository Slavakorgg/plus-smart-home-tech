package ru.yandex.practicum.kafka.telemetry.service.grpc.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;

@Slf4j
@Component
public class SwitchSensorEventGrpcHandler implements SensorEventGrpcHandler {

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        try {
            if (!event.hasSwitchSensorEvent()) {
                log.error("Отсутствуют данные переключателя в событии");
                return;
            }

            SwitchSensorProto sensor = event.getSwitchSensorEvent();

            log.info("Обработано событие переключателя:");
            log.info("ID датчика: {}", event.getId());
            log.info("Хаб: {}", event.getHubId());
            log.info("Состояние переключателя: {}", sensor.getState());

        } catch (Exception e) {
            log.error("Ошибка при обработке события переключателя", e);
        }
    }
}
