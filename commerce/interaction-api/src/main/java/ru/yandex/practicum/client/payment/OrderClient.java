package ru.yandex.practicum.client.payment;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.api.OrderApi;

@FeignClient(
        name = "order",
        configuration = FeignClientConfiguration.class
)
public interface OrderClient extends OrderApi {
}