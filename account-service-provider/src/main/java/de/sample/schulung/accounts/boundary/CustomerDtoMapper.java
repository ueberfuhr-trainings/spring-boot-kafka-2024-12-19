package de.sample.schulung.accounts.boundary;

import de.sample.schulung.accounts.domain.Customer;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class CustomerDtoMapper {

  // TODO Mapper-Generierung mit MapStruct

  public CustomerDto map(Customer source) {
    var result = new CustomerDto();
    result.setUuid(source.getUuid());
    result.setName(source.getName());
    result.setDateOfBirth(source.getDateOfBirth());
    result.setState(this.mapState(source.getState()));
    return result;
  }

  public Customer map(CustomerDto source) {
    var result = new Customer();
    result.setUuid(source.getUuid());
    result.setName(source.getName());
    result.setDateOfBirth(source.getDateOfBirth());
    result.setState(this.mapState(source.getState()));
    return result;
  }

  public String mapState(Customer.CustomerState source) {
    return switch (source) {
      case ACTIVE -> "active";
      case LOCKED -> "locked";
      case DISABLED -> "disabled";
    };
  }

  public Customer.CustomerState mapState(String source) {
    return switch (source) {
      case "active" ->Customer.CustomerState.ACTIVE;
      case "locked" -> Customer.CustomerState.LOCKED;
      case "disabled" -> Customer.CustomerState.DISABLED;
      default -> throw new ValidationException();
    };
  }


}
