package de.sample.schulung.statistics.kafka;

import java.util.UUID;

public record CustomerEventRecord(
  String eventType,
  UUID uuid,
  CustomerRecord customer
) {
}
