package de.sample.schulung.accounts.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.kafka.support.JacksonUtils;
import org.springframework.kafka.support.serializer.JsonDeserializer;

public class CustomJsonDeserializer extends JsonDeserializer<Object> {

  private static ObjectMapper createCustomObjectMapper() {
    final var result = JacksonUtils.enhancedObjectMapper();
    result.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    result.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    result.registerModule(new JavaTimeModule());
    return result;
  }

  public CustomJsonDeserializer() {
    super(createCustomObjectMapper());
  }
}