package de.sample.schulung.accounts;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomersController {

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  Stream<Customer> getCustomers(
    @RequestParam(value = "state", required = false)
    String stateFilter
  ) {
    return Stream.of(
      new Customer(
        UUID.randomUUID(),
        "Max",
        LocalDate.of(2010, Month.FEBRUARY, 10),
        "active"
      ),
      new Customer(
        UUID.randomUUID(),
        "Julia",
        LocalDate.of(2011, Month.APRIL, 2),
        "disabled"
      )
    )
      .filter(customer -> stateFilter == null || stateFilter.equals(customer.getState()));
  }

  @PostMapping(
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseBody
  //@ResponseStatus(HttpStatus.CREATED)
  ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
    customer.setUuid(UUID.randomUUID());
    // TODO use internal collection
    // TODO: Location header: http://localhost:8080/api/v1/customers/{uuid}
    return ResponseEntity
      .created(URI.create("http://localhost:8080/api/v1/customers/" + customer.getUuid()))
      .body(customer);
  }


}
