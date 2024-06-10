package de.sample.schulung.accounts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private UUID uuid;
  private String name;
  @JsonProperty("birthdate")
  private LocalDate dateOfBirth;
  private String state;

}
