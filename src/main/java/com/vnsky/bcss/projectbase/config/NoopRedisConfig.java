package com.vnsky.bcss.projectbase.config;

import com.vnsky.redis.component.RedisStoreOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;

@Configuration
@ConditionalOnProperty(prefix = "application.cache.redisson", name = "enabled", havingValue = "false")
public class NoopRedisConfig {

    private static final Logger log = LoggerFactory.getLogger(NoopRedisConfig.class);

    @Bean
    public RedisStoreOperation redisStoreOperationNoop() {
        log.warn("Redis is disabled by configuration. Using No-Op RedisStoreOperation bean.");

        Class<?> iface = RedisStoreOperation.class;
        return (RedisStoreOperation) Proxy.newProxyInstance(
            iface.getClassLoader(),
            new Class[]{iface},
            (proxy, method, args) -> {
                // No-op for all methods; return sensible defaults
                Class<?> returnType = method.getReturnType();
                if (returnType == Void.TYPE) {
                    return null;
                }
                if (returnType == boolean.class) return false;
                if (returnType == byte.class) return (byte) 0;
                if (returnType == short.class) return (short) 0;
                if (returnType == int.class) return 0;
                if (returnType == long.class) return 0L;
                if (returnType == float.class) return 0f;
                if (returnType == double.class) return 0d;
                if (returnType == char.class) return '\0';
                return null;
            }
        );
    }
}


