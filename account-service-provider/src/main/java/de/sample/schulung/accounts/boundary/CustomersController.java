package de.sample.schulung.accounts.boundary;

import de.sample.schulung.accounts.domain.Customer;
import de.sample.schulung.accounts.domain.CustomersService;
import de.sample.schulung.accounts.domain.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Stream;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomersController {

  private final CustomersService service = new CustomersService();

  @GetMapping(
    produces = MediaType.APPLICATION_JSON_VALUE)
  Stream<Customer> getCustomers(
    @RequestParam(value = "state", required = false)
    String stateFilter
  ) {
    return stateFilter == null
      ?
      this.service
        .getCustomers()
      :
      this.service
        .getCustomersByState(stateFilter);
  }

  @PostMapping(
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  ResponseEntity<Customer> createCustomer(
    @Valid @RequestBody Customer customer
  ) {
    service.createCustomer(customer);
    var uri = linkTo(
      methodOn(CustomersController.class)
        .findCustomerById(customer.getUuid())
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
    return this.service
      .findCustomerById(uuid)
      .orElseThrow(NotFoundException::new);
  }

  @PutMapping(
    value = "/{uuid}",
    consumes = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void replaceCustomer(
    @PathVariable UUID uuid,
    @RequestBody Customer customer) {
    customer.setUuid(uuid);
    service.replaceCustomer(customer);
  }

  @DeleteMapping("/{uuid}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteCustomer(
    @PathVariable UUID uuid
  ) {
    this.service.deleteCustomer(uuid);
  }

}
