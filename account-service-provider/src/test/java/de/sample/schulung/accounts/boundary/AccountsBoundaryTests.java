package de.sample.schulung.accounts.boundary;

import de.sample.schulung.accounts.domain.CustomersService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountsBoundaryTests {

  @Autowired
  MockMvc mvc;
  @MockBean // injiziere Mock (im Controller)
  CustomersService service;

  @Test
  void shouldReturnEmptyArrayWhenNoCustomersExist() throws Exception {

    Mockito.when(service.getCustomers())
        .thenReturn(Stream.empty());

    mvc.perform(
      get("/api/v1/customers")
        .accept(MediaType.APPLICATION_JSON)
    )
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().string("[]"));

  }

}
