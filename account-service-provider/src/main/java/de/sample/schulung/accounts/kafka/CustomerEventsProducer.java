package de.sample.schulung.accounts.kafka;

import de.sample.schulung.accounts.domain.events.CustomerCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerEventsProducer {

  private final KafkaTemplate<UUID, Object> kafkaTemplate;
  private final CustomerEventRecordMapper mapper;
  private final KafkaApplicationConfiguration config;

  @EventListener
  public void handleCustomerCreatedEvent(CustomerCreatedEvent event) {
    var payload = mapper.map(event);
    // async operation
    // - return value is CompletableFuture
    // - https://www.baeldung.com/java-exceptions-completablefuture
    kafkaTemplate.send(
      config.getCustomerEventsTopic(),
      event.customer().getUuid(),
      payload
    );
  }

}
