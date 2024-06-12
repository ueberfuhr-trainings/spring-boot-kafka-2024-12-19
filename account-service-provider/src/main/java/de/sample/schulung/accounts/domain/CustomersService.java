package de.sample.schulung.accounts.domain;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class CustomersService {

  private final Map<UUID, Customer> customers = new HashMap<>();

  // TODO ???
  {
    this.createCustomer(
      new Customer(
        null,
        "Max",
        LocalDate.of(2010, Month.FEBRUARY, 10),
        Customer.CustomerState.ACTIVE
      )
    );
    this.createCustomer(
      new Customer(
        UUID.randomUUID(),
        "Julia",
        LocalDate.of(2011, Month.APRIL, 2),
        Customer.CustomerState.DISABLED
      )
    );
  }

  public Stream<Customer> getCustomers() {
    return customers
      .values()
      .stream();
  }

  public Stream<Customer> getCustomersByState(Customer.CustomerState state) { // TODO enum?
    return this.getCustomers()
      .filter(customer -> state.equals(customer.getState()));
  }

  public void createCustomer(Customer customer) {
    var uuid = UUID.randomUUID();
    customer.setUuid(uuid);
    this.customers.put(customer.getUuid(), customer);
  }

  public Optional<Customer> findCustomerById(UUID uuid) {
    return Optional.ofNullable(
      this.customers.get(uuid)
    );
  }

  public void replaceCustomer(Customer customer) {
    if (this.exists(customer.getUuid())) {
      this.customers.put(customer.getUuid(), customer);
    } else {
      throw new NotFoundException();
    }
  }

  public void deleteCustomer(UUID uuid) {
    if (this.exists(uuid)) {
      this.customers.remove(uuid);
    } else {
      throw new NotFoundException();
    }
  }

  public boolean exists(UUID uuid) {
    return this.customers.containsKey(uuid);
  }

}
