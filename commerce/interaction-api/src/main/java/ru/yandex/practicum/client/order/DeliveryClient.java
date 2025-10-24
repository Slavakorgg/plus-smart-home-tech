package ru.yandex.practicum.client.order;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.api.DeliveryApi;

@FeignClient(
        name = "delivery",
        configuration = FeignClientConfiguration.class
)
public interface DeliveryClient extends DeliveryApi {

}