# Große Dateien versenden

Das Senden großer Dateien über Kafka ist nicht direkt empfohlen, da Kafka hauptsächlich für das Senden von Ereignissen
und Nachrichten mit moderater Größe optimiert ist (typischerweise <1 MB). Dennoch gibt es Möglichkeiten, große Dateien
über Kafka zu versenden, indem sie in kleinere Teile zerlegt oder als externe Referenzen behandelt werden.

Hier sind die gängigsten Ansätze:

## Datei in kleinere Teile zerlegen

Die Datei wird in kleinere Blöcke aufgeteilt, und diese Blöcke werden als separate Nachrichten an Kafka gesendet.

### Implementierungsschritte

1. **Datei segmentieren**:
	- Teilen Sie die Datei in kleinere Teile (z. B. 1 MB pro Segment), um die Kafka-Nachrichtengröße zu begrenzen.
2. **Metadaten hinzufügen**: Jede Nachricht enthält Informationen wie
	- Datei-ID
	- Segmentnummer
	- Gesamtanzahl der Segmente
3. **Zusammensetzen auf der Empfängerseite**:
	- Der Verbraucher liest alle Teile, sortiert sie nach der Segmentnummer und setzt die Datei wieder zusammen.

### Codebeispiel

```java
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class FileProducer {
	private static final int CHUNK_SIZE = 1024 * 1024; // 1 MB

	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
		KafkaProducer<String, byte[]> producer = new KafkaProducer<>(props);
		File file = new File("large-file.txt");
		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] buffer = new byte[CHUNK_SIZE];
			int bytesRead;
			int chunkIndex = 0;
			while ((bytesRead = fis.read(buffer)) > 0) {
				byte[] chunk = bytesRead < CHUNK_SIZE ? new byte[bytesRead] : buffer;
				if (bytesRead < CHUNK_SIZE) {
					System.arraycopy(buffer, 0, chunk, 0, bytesRead);
				}
				// Metadaten in den Schlüssel oder Header einfügen
				String key = "file-id:" + file.getName() + ":chunk:" + chunkIndex++;
				producer.send(new ProducerRecord<>("file-topic", key, chunk));
			}
		}
		producer.close();
	}
}
```

Auf der Konsumentenseite kann die Datei anhand der Metadaten rekonstruiert werden.

## Dateien in externem Speicher ablegen

Anstatt die gesamte Datei direkt über Kafka zu senden, speichern Sie die Datei in einem externen Speicher (z. B. S3,
HDFS, oder einem lokalen Dateisystem) und verwenden Kafka, um die Referenz auf die Datei zu senden.

### Implementierungsschritte

1. Datei hochladen:
	- Laden Sie die Datei in einen externen Speicher hoch (z. B. Amazon S3).
2. Referenz senden:
	- Senden Sie eine Kafka-Nachricht mit Metadaten der Datei, wie:
		- Speicher-URL
		- Authentifizierungsdetails (falls nötig)
		- Dateigröße, Typ, etc.
3. Datei herunterladen:
	- Der Verbraucher liest die Nachricht aus Kafka und lädt die Datei von der angegebenen URL herunter.

### Beispiel

```java
public void produceMessage() {
	// ...
	// Nachricht mit Datei-Referenz senden
	String fileMetadata = """
		{
			"fileName": "large-file.txt",
			"url": "https://s3.amazonaws.com/bucket-name/large-file.txt"
		}
		""";
	producer.send(new ProducerRecord<>("file-topic", "file-metadata", fileMetadata));
}
```

Auf der Konsumentenseite können Sie die Datei anhand der URL abrufen:

```java
public void readFile() {
	// Datei herunterladen
	URL fileUrl = new URL(fileMetadata.getUrl());
	try (InputStream in = fileUrl.openStream();
			 FileOutputStream fos = new FileOutputStream("downloaded-file.txt")) {
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = in.read(buffer)) != -1) {
			fos.write(buffer, 0, bytesRead);
		}
	}
}
```

### Erhöhen der Kafka-Konfiguration für große Nachrichten

Falls es zwingend erforderlich ist, große Dateien direkt zu senden, passen Sie die Kafka-Konfiguration an:

#### Producer-Konfiguration:

```properties
max.request.size=10485760 # 10 MB
```

#### Broker-Konfiguration:

```properties
message.max.bytes=10485760 # 10 MB
```

#### Consumer-Konfiguration:

```properties
fetch.max.bytes=10485760 # 10 MB
```

Dennoch ist dies keine empfohlene Lösung, da es die Kafka-Performance beeinträchtigen kann.

## Fazit

- **Optimale Methode**: Verwenden Sie externe Speicher und senden Sie nur Referenzen.
- **Alternative**: Teilen Sie die Datei in kleinere Blöcke und rekonstruieren Sie sie auf der Empfängerseite.
- **Letzter Ausweg**: Passen Sie Kafka-Konfigurationen an, um größere Nachrichten zu unterstützen.

Durch die Verwendung eines der oben genannten Ansätze können Sie große Dateien effizient über Kafka verarbeiten.

