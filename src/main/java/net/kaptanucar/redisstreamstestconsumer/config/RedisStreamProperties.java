package net.kaptanucar.redisstreamstestconsumer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("redis-stream")
public class RedisStreamProperties {

    private String key;
    private String group;
    private String podInfoKeyPrefix;
    private Integer concurrency;
    private Boolean randomAck;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getPodInfoKeyPrefix() {
        return podInfoKeyPrefix;
    }

    public void setPodInfoKeyPrefix(String podInfoKeyPrefix) {
        this.podInfoKeyPrefix = podInfoKeyPrefix;
    }

    public Integer getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(Integer concurrency) {
        this.concurrency = concurrency;
    }

    public Boolean getRandomAck() {
        return randomAck;
    }

    public void setRandomAck(Boolean randomAck) {
        this.randomAck = randomAck;
    }
}
