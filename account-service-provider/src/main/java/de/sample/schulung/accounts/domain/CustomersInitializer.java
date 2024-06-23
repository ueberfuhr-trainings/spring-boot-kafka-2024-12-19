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
@RequiredArgsConstructor
@Slf4j
public class CustomersInitializer {

  // TODO Enable by Auto-configuration
  // TODO make Customer Sample Provider injectable
  // TODO create one that allows configuration by application.yml

  /*
   * let's use a separate class for the configuration
   *  - default constructor
   *  - injected into the CustomersInitializer
   *  - could be placed in a separate JAVA file too
   */
  @Component
  @ConfigurationProperties(prefix = "application.customers.initialization")
  @Getter
  @Setter
  public static class CustomerInitialerConfiguration {

    private boolean enabled;

  }

  private final CustomersService service;
  private final CustomerInitialerConfiguration config;


  @EventListener(ContextRefreshedEvent.class)
  public void init() {
    if(this.config.enabled && this.service.count() < 1) {
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
