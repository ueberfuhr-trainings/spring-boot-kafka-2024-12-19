# JSON-Schema-Validierung

Um JSON-Nachrichten in Apache Kafka gegen ein JSON Schema zu validieren, wird üblicherweise der Confluent Schema
Registry verwendet. Dieses Tool bietet eine zentrale Speicherung und Validierung von Schemas für Nachrichten, die in
Kafka-Topics veröffentlicht werden.

Hier ist eine schrittweise Anleitung, wie die Validierung funktioniert:

## Einrichtung der Schema Registry

- Installiere die Confluent Schema Registry und starte sie. Sie wird als separater Service bereitgestellt.
- Konfiguriere Kafka-Broker und Producer/Consumer, um mit der Schema Registry zu kommunizieren.

## Definition des JSON Schemas

Ein JSON Schema definiert die Struktur, Typen und Validierungsregeln für die Nachrichten. Beispiel für ein Schema:

```json
{
	"$schema": "http://json-schema.org/draft-07/schema#",
	"title": "User",
	"type": "object",
	"properties": {
		"id": {
			"type": "integer"
		},
		"name": {
			"type": "string"
		},
		"email": {
			"type": "string",
			"format": "email"
		}
	},
	"required": [
		"id",
		"name"
	]
}
```

Dieses Schema wird bei der Schema Registry registriert.

## Registrierung des Schemas

Das JSON Schema wird in der Schema Registry registriert. Dies erfolgt entweder über die REST API oder Tools wie
`ccloud`. Beispiel mit REST API:

```bash
curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
     --data '{
       "schema": "{ \"$schema\": \"http://json-schema.org/draft-07/schema#\", \"type\": \"object\", \"properties\": {\"id\": {\"type\": \"integer\"}, \"name\": {\"type\": \"string\"}} }"
     }' \
     http://localhost:8081/subjects/<topic-name>-value/versions
```

## Produzieren von Nachrichten mit Validierung

Kafka-Producer, die mit der Schema Registry arbeiten, verwenden spezielle Serializer:

- **Avro**: Für Avro-Daten.
- **Protobuf**: Für Protobuf-Daten.
- **JSON Schema**: Für JSON-Daten.

Um JSON-Daten zu serialisieren und gegen ein Schema zu validieren, wird die Bibliothek `kafka-json-schema-serializer`
verwendet. Beispiel in Java:

```java
public void sendWithValidation() {
	Properties props = new Properties();
	props.put("bootstrap.servers", "localhost:9092");
	props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
	props.put("value.serializer", "io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer");
	props.put("schema.registry.url", "http://localhost:8081");
	try (KafkaProducer<String, User> producer = new KafkaProducer<>(props)) {
		User user = new User(1, "Alice", "alice@example.com");
		ProducerRecord<String, User> record = new ProducerRecord<>("user-topic", user);
		producer.send(record);
	}
}
```

Hier wird das JSON Schema automatisch verwendet, um Nachrichten vor dem Versenden zu validieren. Wenn die Nachricht
nicht dem Schema entspricht, wird eine Exception ausgelöst.

## Konsumieren mit Validierung

Consumer nutzen ebenfalls den entsprechenden Deserializer. Beispiel in Java:

```java
public void consumeWithValidation() {
	props.put("value.deserializer", "io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializer");
	try (KafkaConsumer<String, User> consumer = new KafkaConsumer<>(props)) {
		consumer.subscribe(Collections.singletonList("user-topic"));
		while (true) {
			ConsumerRecords<String, User> records = consumer.poll(Duration.ofMillis(100));
			for (ConsumerRecord<String, User> record : records) {
				System.out.println(record.value());
			}
		}
	}
}
```

## Vorteile der JSON Schema Validierung

- **Verlässlichkeit**: Sicherstellung, dass nur gültige Nachrichten in Topics gelangen.
- **Kompatibilität**: Kontrolle über Schema-Evolution (z. B. Backward- oder Forward-Kompatibilität).
- **Zentralisierung**: Ein zentraler Speicherort für alle Schemas, leicht zugänglich.

## Zusammenfassung

1. Schema Registry bereitstellen.
2. JSON Schema definieren und registrieren.
3. Kafka-Producer und -Consumer mit passenden Serializern konfigurieren.
4. Nachrichten werden beim Senden (Producer) und Empfangen (Consumer) validiert.

Dieses Setup gewährleistet, dass alle JSON-Nachrichten in Kafka-Topics dem definierten Schema entsprechen.