package de.sample.schulung.accounts.kafka;

import java.util.UUID;

public record CustomerEventRecord(
  String eventType,
  UUID uuid,
  CustomerRecord customer
) {
}
