package de.sample.schulung.statistics.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
@ConditionalOnProperty(
  name = "application.init-sample-data.enabled",
  havingValue = "true"
)
@RequiredArgsConstructor
public class CustomersInitializer {

  private final CustomersService customersService;

  @EventListener(ContextRefreshedEvent.class)
  public void createSamples() {
    if (customersService.count() < 1) {
      customersService.saveCustomer(
        Customer.builder()
          .uuid(UUID.randomUUID())
          .dateOfBirth(LocalDate.now().minusYears(20).minusDays(100))
          .build()
      );
      customersService.saveCustomer(
        Customer.builder()
          .uuid(UUID.randomUUID())
          .dateOfBirth(LocalDate.now().minusYears(30).minusDays(55))
          .build()
      );
    }
  }

}
