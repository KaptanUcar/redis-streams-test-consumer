package net.kaptanucar.redisstreamstestconsumer.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.stream.StreamListener;

public abstract class StreamObjectListener<T> implements StreamListener<String, MapRecord<String, String, String>> {

    protected final ObjectMapper objectMapper;
    private final Class<T> clazz;

    public StreamObjectListener(ObjectMapper objectMapper, Class<T> clazz) {
        this.objectMapper = objectMapper;
        this.clazz = clazz;
    }

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        T value = objectMapper.convertValue(message.getValue(), clazz);
        onMessage(message.getStream(), message.getId(), value);
    }

    public abstract void onMessage(String stream, RecordId id, T message);
}
