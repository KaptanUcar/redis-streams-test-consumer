package net.kaptanucar.redisstreamstestconsumer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /*@Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private Integer redisPort;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        final var config = new RedisStandaloneConfiguration(redisHost, redisPort);
        return new LettuceConnectionFactory(config).setVerifyPeer();
    }*/

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        final var template = new RedisTemplate<String, String>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setDefaultSerializer(StringRedisSerializer.UTF_8);
        return template;
    }
}
