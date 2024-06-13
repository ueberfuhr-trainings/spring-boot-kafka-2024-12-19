package de.sample.schulung.accounts.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

@Component
@ConfigurationProperties(prefix = "application.customers.initialization")
@RequiredArgsConstructor
@Slf4j
public class CustomersInitializer {

  private final CustomersService service;

  @Getter
  @Setter
  private boolean enabled;

  @EventListener(ContextRefreshedEvent.class)
  public void init() {
    if(this.enabled) {
      log.info("Initializing customers");
      service.createCustomer(
        new Customer(
          null,
          "Max",
          LocalDate.of(2010, Month.FEBRUARY, 10),
          Customer.CustomerState.ACTIVE
        )
      );
      service.createCustomer(
        new Customer(
          UUID.randomUUID(),
          "Julia",
          LocalDate.of(2011, Month.APRIL, 2),
          Customer.CustomerState.DISABLED
        )
      );
    }
  }


}
