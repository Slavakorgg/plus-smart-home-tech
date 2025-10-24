package ru.yandex.practicum.service;

import feign.FeignException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import ru.yandex.practicum.client.cart.WarehouseClient;
import ru.yandex.practicum.dal.ShoppingCart;
import ru.yandex.practicum.dal.ShoppingCartRepository;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.exception.cart.NoActiveShoppingCartException;
import ru.yandex.practicum.exception.cart.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.warehouse.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.yandex.practicum.logging.Logging;

import java.time.Instant;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final WarehouseClient warehouseClient;
    private final TransactionTemplate transactionTemplate;

    @Logging
    @CachePut(value = "carts", key = "#username")
    @Retryable(
            retryFor = {OptimisticLockException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    public ShoppingCartDto addProductsToCart(String username, Map<String, Long> addProductMap) {
        return transactionTemplate.execute(status -> {
            // Получаем активную корзину (предполагаем, что она существует)
            List<ShoppingCart> activeCarts = shoppingCartRepository
                    .findByUsernameAndIsActiveOrderByCreatedAtDesc(username, true);

            ShoppingCart cart;
            if (activeCarts.isEmpty()) {
                cart = new ShoppingCart();
                cart.setUsername(username);
                cart.setIsActive(true);
                cart.setProducts(new HashMap<>(addProductMap));
                cart.setCreatedAt(Instant.now());
            } else {
                cart = activeCarts.getFirst();
                // Объединяем продукты
                Map<String, Long> updatedProducts = new HashMap<>(cart.getProducts());
                for (Map.Entry<String, Long> entry : addProductMap.entrySet()) {
                    updatedProducts.merge(entry.getKey(), entry.getValue(), Long::sum);
                }
                cart.setProducts(updatedProducts);
            }

            // Проверяем наличие товаров на складе
            ShoppingCartDto checkDto = new ShoppingCartDto();
            checkDto.setProducts(cart.getProducts());
            try {
                warehouseClient.checkShoppingCart(checkDto);
            } catch (FeignException.UnprocessableEntity e) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException(e.getMessage());
            }

            // Сохраняем — Hibernate проверит версию автоматически
            shoppingCartRepository.save(cart);
            return ShoppingCartMapper.toDto(cart);
        });
    }

    @Logging
    @CachePut(value = "carts", key = "#username")
    @Transactional
    public ShoppingCartDto removeProductsFromCart(String username, List<String> removeProductList) {
        List<ShoppingCart> activeCarts = shoppingCartRepository
                .findByUsernameAndIsActiveOrderByCreatedAtDesc(username, true);

        if (activeCarts.isEmpty()) {
            throw new NoActiveShoppingCartException("No active shopping carts for user: " + username);
        }

        ShoppingCart cart = activeCarts.getFirst();
        for (String productId : removeProductList) {
            if (!cart.getProducts().containsKey(productId)) {
                throw new NoProductsInShoppingCartException("Product not in cart: " + productId);
            }
            cart.getProducts().remove(productId);
        }

        shoppingCartRepository.save(cart);
        return ShoppingCartMapper.toDto(cart);
    }

    @Logging
    @CachePut(value = "carts", key = "#username")
    @Retryable(
            retryFor = {OptimisticLockException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        return transactionTemplate.execute(status -> {
            List<ShoppingCart> activeCarts = shoppingCartRepository
                    .findByUsernameAndIsActiveOrderByCreatedAtDesc(username, true);

            if (activeCarts.isEmpty()) {
                throw new NoActiveShoppingCartException("No active shopping carts for user: " + username);
            }

            ShoppingCart cart = activeCarts.getFirst();
            if (!cart.getProducts().containsKey(request.getProductId())) {
                throw new NoProductsInShoppingCartException(
                        "Product not in cart: " + request.getProductId()
                );
            }

            // Обновляем количество
            cart.getProducts().put(request.getProductId(), request.getNewQuantity());

            // Проверяем остатки на складе
            ShoppingCartDto checkDto = new ShoppingCartDto();
            checkDto.setProducts(Map.of(request.getProductId(), request.getNewQuantity()));
            try {
                warehouseClient.checkShoppingCart(checkDto);
            } catch (FeignException.UnprocessableEntity e) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException(e.getMessage());
            }

            // Сохраняем — Hibernate проверит версию
            shoppingCartRepository.save(cart);
            return ShoppingCartMapper.toDto(cart);
        });
    }

    @Logging
    @CacheEvict(value = "carts", key = "#username")
    @Transactional
    public String deactivateCart(String username) {
        shoppingCartRepository.deactivateByUsername(username);
        return "true";
    }

    @Logging
    @Cacheable(value = "carts", key = "#username")
    @Transactional(readOnly = true)
    public ShoppingCartDto getCartByUsername(String username) {
        List<ShoppingCart> activeCarts = shoppingCartRepository
                .findByUsernameAndIsActiveOrderByCreatedAtDesc(username, true);

        ShoppingCart cart;
        if (activeCarts.isEmpty()) {
            cart = new ShoppingCart();
            cart.setUsername(username);
            cart.setIsActive(true);
            cart.setCreatedAt(Instant.now());
            cart.setProducts(new HashMap<>());
            shoppingCartRepository.save(cart);
        } else {
            cart = activeCarts.getFirst();
        }
        return ShoppingCartMapper.toDto(cart);
    }
}