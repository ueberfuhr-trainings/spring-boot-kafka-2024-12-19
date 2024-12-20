package de.sample.schulung.accounts.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {

    @Bean
    public NewTopic customerEventsTopic(KafkaApplicationConfiguration config) {
        return TopicBuilder
                .name(config.getCustomerEventsTopic())
                .partitions(config.getCustomerEventsPartitions())
                .replicas(1)
                .build();
    }

}
