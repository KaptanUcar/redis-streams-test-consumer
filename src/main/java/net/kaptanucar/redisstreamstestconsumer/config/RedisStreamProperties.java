package net.kaptanucar.redisstreamstestconsumer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("redis-stream")
public class RedisStreamConfig {

    private String key;
    private Integer concurrency;

    public String getKey() {
        return key;
    }

    public Integer getConcurrency() {
        return concurrency;
    }
}
