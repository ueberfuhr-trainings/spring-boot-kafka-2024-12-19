package de.sample.schulung.accounts.kafka;

import de.sample.schulung.accounts.domain.Customer;
import de.sample.schulung.accounts.domain.Customer.CustomerState;
import de.sample.schulung.accounts.domain.events.CustomerCreatedEvent;
import de.sample.schulung.accounts.domain.events.CustomerDeletedEvent;
import de.sample.schulung.accounts.domain.events.CustomerReplacedEvent;
import org.springframework.stereotype.Component;

@Component
public class CustomerEventRecordMapper {

  public String map(CustomerState state) {
    return switch (state) {
      case ACTIVE -> "active";
      case LOCKED -> "locked";
      case DISABLED -> "disabled";
    };
  }

  public CustomerRecord map(Customer customer) {
    return new CustomerRecord(
      customer.getName(),
      customer.getDateOfBirth(),
      this.map(customer.getState())
    );
  }

  public CustomerEventRecord map(CustomerCreatedEvent event) {
    var customer = event.customer();
    return new CustomerEventRecord(
      "created",
      customer.getUuid(),
      this.map(customer)
    );
  }

  public CustomerEventRecord map(CustomerReplacedEvent event) {
    var customer = event.customer();
    return new CustomerEventRecord(
      "replaced",
      customer.getUuid(),
      this.map(customer)
    );
  }

  public CustomerEventRecord map(CustomerDeletedEvent event) {
    return new CustomerEventRecord(
      "deleted",
      event.uuid(),
      null
    );
  }

}
