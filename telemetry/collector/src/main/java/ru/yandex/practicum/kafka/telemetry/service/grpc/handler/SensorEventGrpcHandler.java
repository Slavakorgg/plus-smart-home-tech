package ru.yandex.practicum.kafka.telemetry.service.grpc.handler;


import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventGrpcHandler {
    SensorEventProto.PayloadCase getMessageType();
    void handle(SensorEventProto event);
}
