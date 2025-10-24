package ru.yandex.practicum.client.order;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.api.WarehouseApi;

@FeignClient(name = "warehouse")
public interface WarehouseClient extends WarehouseApi {
}