package de.sample.schulung.statistics.kafka.exceptions;

import org.springframework.kafka.support.serializer.FailedDeserializationInfo;

import java.util.function.Function;

public class CustomDeserializationFailureHandler<T>
  implements Function<FailedDeserializationInfo, T> {

  @Override
  public T apply(FailedDeserializationInfo failedDeserializationInfo) {
    // hier Fehler analysieren und eigenes Objekt erzeugen
    byte[] payload = failedDeserializationInfo.getData();
    return null; // TODO
  }

}
