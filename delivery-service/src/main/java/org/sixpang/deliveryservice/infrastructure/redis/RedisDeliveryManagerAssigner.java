package org.sixpang.deliveryservice.infrastructure.redis;

import org.sixpang.deliveryservice.application.service.DeliveryManagerAssigner;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RedisDeliveryManagerAssigner implements DeliveryManagerAssigner {

    @Qualifier("deliveryRedisTemplate")
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Optional<String> getNextIdWithRotation(String key) {
        return Optional.ofNullable(redisTemplate.opsForList().rightPopAndLeftPush(key, key));
    }

    @Override
    public Optional<String> getNextId(String key) {
        return Optional.ofNullable(redisTemplate.opsForList().rightPop(key));
    }

    public RedisDeliveryManagerAssigner(@Qualifier("deliveryRedisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void refreshCache(String key, List<String> ids) {
        redisTemplate.delete(key);
        if (!ids.isEmpty()) {
            redisTemplate.opsForList().leftPushAll(key, ids);
        }
    }
}
