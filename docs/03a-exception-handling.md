# Exception Handling

Das Exception Handling in Apache Kafka ist ein wichtiger Aspekt, um sicherzustellen, dass Fehler während der
Nachrichtenverarbeitung robust und effizient behandelt werden. In der Regel müssen Fehler beim Produzieren, Konsumieren
oder Verarbeiten von Nachrichten behandelt werden.

## Exception Handling beim Produzieren

Fehler beim Produzieren von Nachrichten können auftreten, z. B. durch:

- Netzwerkprobleme
- Broker-Ausfälle
- Falsche Konfigurationen

### Ansatz: Callback-Funktion

Verwenden Sie die Callback-Funktion des Kafka-Producer-API, um Fehler abzufangen.

### Beispiel

```java
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;

public class ProducerWithExceptionHandling {
	public static void main(String[] args) {
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		KafkaProducer<String, String> producer = new KafkaProducer<>(props);
		ProducerRecord<String, String> record = new ProducerRecord<>("my-topic", "key", "value");
		producer.send(record, (RecordMetadata metadata, Exception exception) -> {
			if (exception != null) {
				System.err.println("Fehler beim Senden der Nachricht: " + exception.getMessage());
				// Fehlerbehandlung wie Logging oder Retry-Mechanismus
			} else {
				System.out.println("Nachricht erfolgreich gesendet: " + metadata.offset());
			}
		});
		producer.close();
	}
}
```

## Exception Handling beim Konsumieren

Fehler beim Konsumieren können auftreten, z. B. durch:

- Ungültige Nachrichten
- Deserialisierungsprobleme
- Probleme in der Verarbeitungslogik

### Spring Kafka: Exception Handling mit Error Handler

Spring Kafka bietet verschiedene Mechanismen, um Fehler beim Konsumieren zu behandeln.

#### DefaultErrorHandler

Der DefaultErrorHandler (ehemals SeekToCurrentErrorHandler) ermöglicht das Wiederholen von fehlgeschlagenen Nachrichten
oder das Überspringen fehlerhafter Nachrichten.

**Konfiguration:**

```java
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.KafkaException;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.converter.ConversionException;

import java.util.function.BiConsumer;

public class KafkaConfig {

	@Bean
	public DefaultErrorHandler errorHandler() {
		// Wiederholungslogik für bestimmte Ausnahmen
		return new DefaultErrorHandler((record, exception) -> {
			System.err.println("Fehler beim Verarbeiten der Nachricht: " + record.value());
			System.err.println("Grund: " + exception.getMessage());
			// Hier kann die Nachricht geloggt, gespeichert oder verworfen werden.
		});
	}

}
```

#### Retry Mechanism

Spring Kafka bietet integrierte Unterstützung für das erneute Verarbeiten fehlgeschlagener Nachrichten.

**Konfiguration mit `RetryTemplate`:**

```java
import org.springframework.context.annotation.Bean;
import org.springframework.retry.support.RetryTemplate;

@Bean
public RetryTemplate retryTemplate() {
	return RetryTemplate.builder()
		.maxAttempts(3)
		.fixedBackoff(1000) // 1 Sekunde Wartezeit zwischen den Versuchen
		.build();
}
```

#### Dead Letter Topic (DLT)

Wenn eine Nachricht nach mehreren Versuchen nicht verarbeitet werden kann, wird sie in ein spezielles Dead Letter Topic
geschrieben.

**Konfiguration:**

```java
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ProducerFactory;

@Bean
public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
	ProducerFactory<String, String> producerFactory) {
	DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
		new KafkaTemplate<>(producerFactory));
	DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer);
	ConcurrentKafkaListenerContainerFactory<String, String> factory =
		new ConcurrentKafkaListenerContainerFactory<>();
	factory.setCommonErrorHandler(errorHandler);
	return factory;
}
```

Nachrichten, die in das Dead Letter Topic geschrieben werden, können später analysiert oder erneut verarbeitet werden.

#### Beispiel: Fehlerhandling in einem Listener

```java
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

	@KafkaListener(
		topics = "my-topic",
		groupId = "my-group"
	)
	public void consume(ConsumerRecord<String, String> record) {
		// Nachricht verarbeiten
		System.out.println("Empfangene Nachricht: " + record.value());
		// ...
		// Beispiel: Fehler in der Verarbeitung
		throw new ListenerExecutionFailedException("Nachricht nicht verarbeitet");
	}
}
```

## Allgemeine Strategien

1. Retries:
	- Kafka kann automatisch Wiederholungen durchführen, wenn der enable.auto.commit deaktiviert ist und die Nachricht als
		nicht verarbeitet markiert wird.
2. Backoff-Strategie:
	- Zwischen Wiederholungsversuchen können Sie eine Backoff-Zeit festlegen, um die Belastung des Systems zu verringern.
3. Monitoring:
	- Verwenden Sie Monitoring-Tools wie Prometheus oder Kafka Exporter, um fehlerhafte Nachrichten zu überwachen.
4. Logging und Alerting:
	- Protokollieren Sie Fehler und richten Sie Alarme ein, um kritische Probleme schnell zu erkennen.

## Fazit

Das Exception Handling mit Kafka kann flexibel an die Anforderungen Ihrer Anwendung angepasst werden. Mit Spring Kafka
stehen mächtige Mechanismen wie der DefaultErrorHandler, RetryTemplates und Dead Letter Topics zur Verfügung, um
Nachrichten zuverlässig zu verarbeiten und Fehler zu handhaben.


