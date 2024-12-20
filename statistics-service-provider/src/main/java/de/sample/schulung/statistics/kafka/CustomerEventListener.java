package de.sample.schulung.statistics.kafka;

import de.sample.schulung.statistics.domain.Customer;
import de.sample.schulung.statistics.domain.CustomersService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerEventListener {

  private final CustomersService customersService;

  @KafkaListener(
    topics = "${application.kafka.customer-events-topic}"
  )
  public void consume(
    @Payload CustomerEventRecord record,
    @Header(KafkaHeaders.RECEIVED_PARTITION) int partition
  ) {
    log.info(
      "Received record: {} {} from partition: {}",
      record.eventType(),
      record.uuid(),
      partition
    );
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
  }

}
