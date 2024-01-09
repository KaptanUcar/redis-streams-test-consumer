package net.kaptanucar.redisstreamstestconsumer.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.kaptanucar.redisstreamstestconsumer.config.RedisStreamProperties;
import net.kaptanucar.redisstreamstestconsumer.model.DummyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.ThreadLocalRandom;

public class DummyStreamListener extends StreamMessageListener<DummyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(DummyStreamListener.class);

    private final String consumerName;
    private final RedisStreamProperties redisStreamProperties;
    private final RedisTemplate<String, String> redisTemplate;

    public DummyStreamListener(
            String consumerName,
            RedisStreamProperties redisStreamProperties,
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper
    ) {
        super(objectMapper, DummyEvent.class);

        this.consumerName = consumerName;
        this.redisStreamProperties = redisStreamProperties;
        this.redisTemplate = redisTemplate;

        logger.info("DummyStreamListener {} has been created.", consumerName);
    }

    @Override
    public void onMessage(String stream, RecordId id, DummyEvent message) {
        logger.info(
                "[{}] Received message from stream {} => Id: {} Message: {} ",
                consumerName,
                stream,
                id,
                message
        );

        if (!redisStreamProperties.getRandomAck() || ThreadLocalRandom.current().nextBoolean()) {
            redisTemplate.opsForStream()
                    .acknowledge(redisStreamProperties.getKey(), redisStreamProperties.getGroup(), id);

            logger.info("[{}] Acknowledged message {}.", consumerName, id);
        }
    }
}
