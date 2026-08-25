package com.rikkei.ss11.b4.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class LangfuseConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("prompts");
    }
}
