package de.sample.schulung.accounts.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application.kafka")
@Getter
@Setter
public class KafkaApplicationConfiguration {

  private String customerEventsTopic = "customer-events";

}
