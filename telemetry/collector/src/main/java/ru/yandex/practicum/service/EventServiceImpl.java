package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.hub.HubEvent;
import ru.yandex.practicum.dto.sensor.SensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.mapper.GrpcHubEventMapper;
import ru.yandex.practicum.mapper.GrpcSensorEventMapper;
import ru.yandex.practicum.mapper.HubEventMapper;
import ru.yandex.practicum.mapper.SensorEventMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final Producer<Void, SpecificRecordBase> producer;

    @Value("${smart-home-tech.kafka.hub-event-topic}")
    private String hubEventTopicName;

    @Value("${smart-home-tech.kafka.sensor-event-topic}")
    private String sensorEventTopicName;

    // SENSOR EVENTS

    @Override
    public ResponseEntity<Void> handleSensorEvent(SensorEvent sensorEvent) {
        log.debug("Received: {}", sensorEvent);
        SpecificRecordBase avroSensorEvent = SensorEventMapper.toAvro(sensorEvent);
        ProducerRecord<Void, SpecificRecordBase> record = new ProducerRecord<>(sensorEventTopicName, avroSensorEvent);
        producer.send(record);
        log.debug("Sent: {}", avroSensorEvent);
        return ResponseEntity.ok().build();
    }

    @Override
    public void handleSensorEvent(SensorEventProto sensorEventProto) {
        log.debug("Received: {}", sensorEventProto);
        SpecificRecordBase avroSensorEvent = GrpcSensorEventMapper.toAvro(sensorEventProto);
        ProducerRecord<Void, SpecificRecordBase> record = new ProducerRecord<>(sensorEventTopicName, avroSensorEvent);
        producer.send(record);
        log.debug("Sent: {}", avroSensorEvent);
    }

    // HUB EVENTS

    @Override
    public ResponseEntity<Void> handleHubEvent(HubEvent hubEvent) {
        log.debug("Received: {}", hubEvent);
        SpecificRecordBase avroHubEvent = HubEventMapper.toAvro(hubEvent);
        ProducerRecord<Void, SpecificRecordBase> record = new ProducerRecord<>(hubEventTopicName, avroHubEvent);
        producer.send(record);
        log.debug("Sent: {}", avroHubEvent);
        return ResponseEntity.ok().build();
    }

    @Override
    public void handleHubEvent(HubEventProto hubEventProto) {
        log.debug("Received: {}", hubEventProto);
        SpecificRecordBase avroHubEvent = GrpcHubEventMapper.toAvro(hubEventProto);
        ProducerRecord<Void, SpecificRecordBase> record = new ProducerRecord<>(hubEventTopicName, avroHubEvent);
        producer.send(record);
        log.debug("Sent: {}", avroHubEvent);
    }

}