package de.sample.schulung.accounts;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomersController {

  private final Map<UUID, Customer> customers = new HashMap<>();

  {
    this.createCustomer(
      new Customer(
        null,
        "Max",
        LocalDate.of(2010, Month.FEBRUARY, 10),
        "active"
      )
    );
    this.createCustomer(
      new Customer(
        UUID.randomUUID(),
        "Julia",
        LocalDate.of(2011, Month.APRIL, 2),
        "disabled"
      )
    );
  }

  @GetMapping(
    produces = MediaType.APPLICATION_JSON_VALUE)
  Stream<Customer> getCustomers(
    @RequestParam(value = "state", required = false)
    String stateFilter
  ) {
    return this.customers
      .values()
      .stream()
      .filter(customer -> stateFilter == null || stateFilter.equals(customer.getState()));
  }

  @PostMapping(
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  ResponseEntity<Customer> createCustomer(
    @RequestBody Customer customer
  ) {
    var uuid = UUID.randomUUID();
    customer.setUuid(uuid);
    this.customers.put(uuid, customer);
    var uri = linkTo(
      methodOn(CustomersController.class)
        .findCustomerById(uuid)
    ).toUri();
    return ResponseEntity
      .created(uri)
      .body(customer);
  }

  @GetMapping(
    value = "/{uuid}",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  Customer findCustomerById(
    @PathVariable UUID uuid
  ) {
    return this.customers
      .get(uuid);
  }

  @PutMapping(
    value = "/{uuid}",
    consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void replaceCustomer(
    @PathVariable UUID uuid,
    @RequestBody Customer customer) {
    if (this.customers.containsKey(uuid)) {
      customer.setUuid(uuid);
      this.customers.put(uuid, customer);
    }

  }


}
