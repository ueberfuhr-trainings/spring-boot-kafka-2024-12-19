# Dead Letter Topics (DLTs)

Ein Dead Letter Topic (DLT, auch DLQ…”Dead Letter Queue”) in Apache Kafka ist ein spezielles Kafka-Topic, das verwendet
wird, um Nachrichten zu speichern, die aus irgendeinem Grund nicht erfolgreich verarbeitet werden können. Es dient als
Mechanismus zur Fehlerbehandlung und Nachverfolgung in Kafka-basierten Systemen.

## Warum Dead Letter Topics verwenden?

- **Fehlerhafte Nachrichten**: Nachrichten können fehlerhaft sein, z. B. aufgrund von:
    - Datenvalidierungsproblemen.
    - Nicht behandelbaren Verarbeitungsfehlern (Exceptions).
    - Zeitüberschreitungen oder anderen externen Fehlern.
- **Verarbeitungsfluss schützen**:
    - Fehlerhafte Nachrichten können den normalen Verarbeitungsfluss stören. Durch das Verschieben solcher Nachrichten
      in
      ein DLT wird die reguläre Verarbeitung entkoppelt.
- **Fehleranalyse**:
    - Nachrichten im DLT enthalten Informationen über den Fehler und die Nachricht selbst, was eine spätere Untersuchung
      und Wiederverarbeitung ermöglicht.

## Wie funktioniert ein Dead Letter Topic?

1. **Nachrichtenkonsum und Fehlererkennung**:
    - Ein Consumer liest Nachrichten aus dem ursprünglichen Kafka-Topic.
    - Tritt während der Verarbeitung ein Fehler auf, wird die Nachricht identifiziert.
2. **Verschieben ins DLT**:
    - Die fehlerhafte Nachricht wird in ein speziell konfiguriertes DLT geschrieben.
    - Oft werden zusätzliche Metadaten hinzugefügt, z. B. der Grund für den Fehler.
3. **Separate Analyse oder Wiederverarbeitung**:
    - Nachrichten im DLT können später analysiert, manuell korrigiert oder automatisiert erneut verarbeitet werden.

## Verantwortung für Dead Letter Topics

Das Senden fehlerhafter Nachrichten in ein Dead Letter Topic (DLT) bei Kafka ist die Verantwortung der Verbraucher (
Consumers), insbesondere der Logik, die die Verarbeitung der Nachrichten steuert. Die genaue Implementierung hängt von
den verwendeten Frameworks und der Architektur ab.

### Consumer-Anwendung (Manuell implementiert):

In einer benutzerdefinierten Consumer-Anwendung liegt die Verantwortung für das Erkennen von Fehlern und das
Weiterleiten in ein DLT beim Entwickler.

Typische Vorgehensweise:

- Die Nachricht wird aus dem Kafka-Topic konsumiert.
- Fehlerhafte Nachrichten werden identifiziert (z. B. durch Exceptions).
- Die fehlerhafte Nachricht wird explizit in das Dead Letter Topic geschrieben.

Beispiel:

```java
public void consume() {
    // ...
    try {
        processMessage(record);
    } catch (Exception e) {
        producer.send(
                new ProducerRecord<>(
                        "dead-letter-topic",
                        record.key(),
                        record.value()
                )
        );
    }
}
```

### Frameworks (z. B. Spring Kafka):

Frameworks wie Spring Kafka bieten eingebaute Unterstützung für Dead Letter Topics. Wenn die Verarbeitung einer
Nachricht fehlschlägt (z. B. durch eine Exception), wird sie automatisch in ein konfiguriertes DLT verschoben. Die
Konfiguration erfolgt durch:

- `ErrorHandler`: Ein spezifischer `DeadLetterPublishingRecoverer` kümmert sich um das Weiterleiten.
- `Retries` und `Recovery`: Nachrichten können vor dem DLT mehrfach verarbeitet werden.

Beispiel mit Spring Kafka:

```java

@Bean
public DeadLetterPublishingRecoverer recoverer(KafkaTemplate<Object, Object> template) {
    return new DeadLetterPublishingRecoverer(
            template,
            (record, exception) -> new TopicPartition(
                    "dead-letter-topic",
                    record.partition()
            )
    );
}

@Bean
public SeekToCurrentErrorHandler errorHandler(DeadLetterPublishingRecoverer recoverer) {
    return new SeekToCurrentErrorHandler(
            recoverer,
            3 // Nach 3 Versuchen ins DLT senden
    );
}
```

### Middleware oder Stream Processing Tools:

Tools wie Kafka Streams, Confluent Kafka Connect, oder Flink können Dead Letter Topics automatisch verwalten.

Beispiel:

- Kafka Connect schreibt fehlerhafte Nachrichten, die nicht verarbeitet werden können (z. B. durch fehlerhafte
  Transformationen), automatisch in ein DLT.
- Die Konfiguration erfolgt über `errors.deadletterqueue.topic.name`.

Beispiel-Konfiguration in Kafka Connect:

```properties
errors.deadletterqueue.topic.name=dead-letter-topic
errors.deadletterqueue.context.headers.enable=true
errors.tolerance=all
```

### Produzenten (Producer) – In Ausnahmefällen:

Normalerweise sollten Produzenten keine Nachrichten in ein DLT senden.

In seltenen Fällen, wenn der Produzent selbst Fehler erkennt (z. B. durch Vorab-Validierungen oder externe Systeme),
kann er die Nachricht direkt in ein DLT schreiben.

## Best Practices für Dead Letter Topics

1. **Richtige Verantwortlichkeit**:
    - Die Verantwortung für das Erkennen fehlerhafter Nachrichten liegt meist beim Consumer, da dieser die
      Geschäftslogik kennt und entscheiden kann, ob eine Nachricht fehlerhaft ist.
2. **DLT-Namen und Partitionierung**:
    - Standardisieren Sie den Namen des DLT (z. B. `{original_topic_name}-dlt`).
    - Behalten Sie die gleiche Partitionierungsstrategie bei wie das Original-Topic, um eine nachvollziehbare
      Verarbeitung zu gewährleisten.
3. **Metadaten hinzufügen**:
    - Beim Verschieben in ein DLT sollten Informationen wie der Grund des Fehlers, der Zeitpunkt und die ursprüngliche
      Partition gespeichert werden. Dies erleichtert spätere Analysen.
4. **Monitoring und Alerts**:
    - Überwachen Sie das Dead Letter Topic, um systematische Fehler oder Probleme in der Verarbeitung frühzeitig zu
      erkennen.

## Zusammenfassung

Die Verantwortung für das Verschieben fehlerhafter Nachrichten in ein Dead Letter Topic liegt typischerweise beim
Consumer. Je nach eingesetztem Framework oder Tool kann dies manuell, durch eine integrierte Fehlerbehandlungslogik (wie
in Spring Kafka), oder durch Middleware wie Kafka Connect erfolgen. Der Producer hat in der Regel keine Rolle in der
direkten Handhabung von Dead Letter Topics.