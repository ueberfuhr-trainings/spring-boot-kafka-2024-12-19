package de.sample.schulung.accounts.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomersInitializer {

  private final CustomersService service;

  @EventListener(ContextRefreshedEvent.class)
  public void init() {
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
