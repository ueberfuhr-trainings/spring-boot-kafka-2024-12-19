# Wofür Kafka?

Apache Kafka ist eine verteilte Streaming-Plattform, die häufig für das Verarbeiten und Übertragen von Datenströmen in
Echtzeit verwendet wird.

Es wird in verschiedenen Szenarien eingesetzt, darunter:

## Echtzeit-Datenverarbeitung

Kafka wird genutzt, um Datenströme in Echtzeit zu sammeln, zu speichern und zu verarbeiten. Beispiele:

- Finanztransaktionen überwachen: Banken nutzen Kafka, um verdächtige Aktivitäten sofort zu erkennen.
- IoT-Geräte überwachen: Sensoren können kontinuierlich Daten an Kafka senden, die dann verarbeitet werden.

## Messaging-System

Kafka fungiert als Message-Broker zwischen verschiedenen Anwendungen oder Diensten. Es ist skalierbar und hoch
performant und wird oft anstelle traditioneller Message Queues wie RabbitMQ eingesetzt. Beispiele:

- Log-Verarbeitung: Anwendungen senden Logs an Kafka, wo sie gesammelt und analysiert werden können.
- Ereignisbasierte Kommunikation: Microservices kommunizieren über Kafka, indem sie Events veröffentlichen und
  abonnieren.

## Datenintegration

Kafka dient als Daten-Hub, der Daten aus unterschiedlichen Quellen zentralisiert und an Zielsysteme verteilt. Beispiele:

- Datenbankänderungen verfolgen: Änderungen in einer Datenbank können mit Kafka an andere Systeme weitergeleitet werden.
- Daten in Data Warehouses laden: Datenströme von verschiedenen Systemen können in Echtzeit in analytische Systeme wie
  Snowflake oder Apache Hive integriert werden.

## Speicherung von Datenströmen

Kafka speichert Datenströme in sogenannten Topics und erlaubt deren Verarbeitung später. Diese Persistenz ermöglicht:

- Wiederholte Verarbeitung: Daten können mehrmals verarbeitet werden, falls ein Fehler auftritt.
- Langfristige Speicherung: Kafka kann als Datenarchiv für Streams fungieren.

## Typische Anwendungsfälle

- E-Commerce-Plattformen: Echtzeit-Verarbeitung von Bestellungen, Lagerbeständen und Benutzeraktivitäten.
- Social Media: Verarbeitung von Likes, Kommentaren und Posts in Echtzeit.
- Medien-Streaming: Übertragung von Datenströmen, z. B. für Video- oder Audiostreaming.
- Big Data: Daten aus verschiedenen Quellen in eine zentrale Plattform wie Hadoop oder Spark integrieren.

Kafka ist besonders geeignet, wenn es ankommt auf:

- Hohe Durchsatzrate
- Niedrige Latenz
- Skalierbarkeit
- Verlässliche Datenübertragung