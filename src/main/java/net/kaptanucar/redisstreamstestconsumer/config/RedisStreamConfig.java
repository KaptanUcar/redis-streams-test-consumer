package net.kaptanucar.redisstreamstestconsumer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.kaptanucar.redisstreamstestconsumer.listener.DummyStreamListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@Configuration
@Profile("!test")
public class RedisStreamConfig {

    private static final Predicate<Throwable> IGNORE_ALL_ERRORS = t -> false;

    private final RedisStreamProperties properties;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("#{environment['HOSTNAME']}")
    private String kubernetesPodName;

    public RedisStreamConfig(
            RedisStreamProperties properties,
            ObjectMapper objectMapper,
            RedisTemplate<String, String> redisTemplate
    ) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
    }

    @Bean
    public List<Subscription> redisStreamSubscriptions(RedisConnectionFactory redisConnectionFactory) {
        refreshRedisKey();

        final var options = StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofSeconds(5))
                .build();

        final var container = StreamMessageListenerContainer.create(redisConnectionFactory, options);

        final var subscriptions = IntStream.range(0, properties.getConcurrency())
                .mapToObj((index) -> createSubscription(container, index))
                .toList();

        container.start();

        return subscriptions;
    }

    private Subscription createSubscription(
            StreamMessageListenerContainer<String, MapRecord<String, String, String>> container,
            int index
    ) {
        final var consumerName = String.format("%s_%s", kubernetesPodName, index);

        final var streamReadRequest = StreamMessageListenerContainer.StreamReadRequest
                .builder(StreamOffset.create(properties.getKey(), ReadOffset.lastConsumed()))
                //.builder(StreamOffset.fromStart(properties.getKey())),
                .consumer(Consumer.from(properties.getGroup(), consumerName))
                .autoAcknowledge(false)
                .cancelOnError(IGNORE_ALL_ERRORS)
                .build();

        return container.register(
                streamReadRequest,
                new DummyStreamListener(consumerName, properties, redisTemplate, objectMapper)
        );
    }

    @Scheduled(fixedDelay = 3, timeUnit = TimeUnit.SECONDS)
    public void refreshRedisKey() {
        final var key = String.format("%s:%s", properties.getPodInfoKeyPrefix(), kubernetesPodName);

        final var value = objectMapper.createObjectNode();
        value.put("concurrency", properties.getConcurrency());
        value.put("lastHeartbeatTime", System.currentTimeMillis());

        redisTemplate.opsForValue().set(key, value.toString(), 5, TimeUnit.SECONDS);
    }
}
