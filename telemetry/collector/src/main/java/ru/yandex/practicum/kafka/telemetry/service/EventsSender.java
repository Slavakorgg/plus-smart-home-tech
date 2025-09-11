package ru.yandex.practicum.kafka.telemetry.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEvent;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.model.hub.events.BaseHubEvent;
import ru.yandex.practicum.kafka.telemetry.model.sensors.SensorEvent;

@Component
public class EventsSender {

    private final EventProducer producer;

    @Value("${app.kafka.topics.sensor-events}")
    private String sensorTopic;

    @Value("${app.kafka.topics.hub-events}")
    private String hubTopic;

    public EventsSender(EventProducer producer) {
        this.producer = producer;
    }

    public void sendSensorEvent(SensorEvent someEvent, Object payload) {
        // Общее событие
        SensorEventAvro avroEvent = new SensorEventAvro();
        avroEvent.setId(someEvent.getId());
        avroEvent.setHubId(someEvent.getHubId());
        avroEvent.setTimestamp(someEvent.getTimestamp().toEpochMilli());
        avroEvent.setPayload(payload); // Данные датчика

        producer.sendAvroEvent(sensorTopic, avroEvent);
    }

    public void sendHubEvent(BaseHubEvent someEvent, Object payload) {
        // Общее событие
        HubEvent avroEvent = new HubEvent();
        avroEvent.setHubId(someEvent.getHubId());
        avroEvent.setTimestamp(someEvent.getTimestamp().toEpochMilli());
        avroEvent.setPayload(payload); // Данные датчика

        producer.sendAvroEvent(hubTopic, avroEvent);
    }
}
