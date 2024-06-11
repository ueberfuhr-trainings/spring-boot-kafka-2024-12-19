package de.sample.schulung.accounts;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class AccountsApiTests {

  @Autowired
  MockMvc mvc;

  @Test
  void shouldReturnCustomers() throws Exception {
    mvc.perform(
        get("/api/v1/customers")
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void shouldNotReturnCustomersWithXml() throws Exception {
    mvc.perform(
        get("/api/v1/customers")
          .accept(MediaType.APPLICATION_XML)
      )
      .andExpect(status().isNotAcceptable());
  }

  @Test
  void shouldCreateCustomer() throws Exception {
    var location = mvc.perform(
        post("/api/v1/customers")
          .contentType(MediaType.APPLICATION_JSON)
          .content("""
            {
              "name": "Tom Mayer",
              "birthdate": "1985-07-30",
              "state": "active"
            }
            """)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isCreated())
      .andExpect(header().exists("Location"))
      .andReturn()
      .getResponse()
      .getHeader("Location");

    assertThat(location)
      .isNotBlank();

    mvc.perform(
        get(location)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("Tom Mayer"));
  }

  // TODO: DELETE /customers/{uuid}

}
