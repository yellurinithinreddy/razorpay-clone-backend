package com.nithin.razorpay.common.config;

import com.nithin.razorpay.common.enums.EventAggregateType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "app.kafka")
@Setter
public class KafkaProperties {

    private Map<String , String> topics = new HashMap<>();

    public String topicFor(EventAggregateType aggregateType) {
        String topic = topics.get(aggregateType.name().toLowerCase());
        if(topic == null){
            throw new IllegalArgumentException("No Kafka Topic is configured for aggregate type: "+aggregateType);
        }

        return topic;
    }
}
