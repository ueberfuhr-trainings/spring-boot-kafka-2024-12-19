package de.sample.schulung.statistics.kafka;

import de.sample.schulung.statistics.domain.Customer;
import de.sample.schulung.statistics.domain.CustomersService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerEventListener {

  private final CustomersService customersService;

  /*
   * Auto-creates or needs the following topics:
   *  - customer-events-retry-500-0
   *  - customer-events-retry-1000-0
   *  - customer-events-retry-2000-0
   *  - customer-events-retry-4000-0
   *  - customer-events-dlt-0
   */
  @RetryableTopic(
    attempts = "5",
    backoff = @Backoff(
      delay = 500L,
      multiplier = 2
    )
  )
  @KafkaListener(
    topics = "${application.kafka.customer-events-topic}"
  )
  public void consume(
    @Payload CustomerEventRecord record,
    @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
    Acknowledgment acknowledgement
  ) {
    log.info(
      "Received record: {} {} from partition: {}",
      record.eventType(),
      record.uuid(),
      partition
    );
    // Use this to test a processing error.
    // if (true) {
    //   throw new RuntimeException("processing error");
    // }
    switch (record.eventType()) {
      case "created":
      case "replaced":
        if ("active".equals(record.customer().state())) {
          var customer = Customer
            .builder()
            .uuid(record.uuid())
            .dateOfBirth(record.customer().birthdate())
            .build();
          customersService.saveCustomer(customer);
        } else {
          // TODO wenn "created" / nicht "active" -> kein DB-Zugriff
          customersService.deleteCustomer(record.uuid());
        }
        break;
      case "deleted":
        customersService.deleteCustomer(record.uuid());
        break;
      default:
        throw new ValidationException();
    }
    acknowledgement.acknowledge();
  }

}
