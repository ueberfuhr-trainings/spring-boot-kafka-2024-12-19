# Acknowledgements

In Apache Kafka gibt es verschiedene Acknowledgement-Arten (Ack-Levels), die steuern, wie ein Kafka-Producer
Bestätigungen (Acknowledgements) von Kafka-Brokern erhält, um die Speicherung einer Nachricht zu bestätigen. Diese
Einstellungen beeinflussen die Zuverlässigkeit, Latenz und den Durchsatz des Systems.

## `acks=0` (Keine Bestätigung)

- **Funktionsweise**:
  - Der Producer wartet nicht auf eine Bestätigung vom Broker.
  - Sobald die Nachricht in den internen Puffer des Producers geschrieben wurde, gilt sie als "gesendet".
- **Vorteile**:
  - Maximale Geschwindigkeit, da keine Wartezeit.
  - Geringe Latenz.
- **Nachteile**:
  - Keine Garantie, dass die Nachricht den Broker erreicht hat.
  - Nachrichten können verloren gehen, wenn der Broker oder das Netzwerk ausfällt.
- **Einsatzbereich**:
  - Für Szenarien, in denen Datenverlust tolerierbar ist (z. B. unkritische Logs).

## `acks=1` (Bestätigung durch den Leader)

- **Funktionsweise**:
  - Der Producer wartet, bis der Leader der Partition die Nachricht erfolgreich geschrieben hat.
  - Es wird nicht überprüft, ob die Nachricht auf die Replikate synchronisiert wurde.
- **Vorteile**:
  - Höhere Zuverlässigkeit als `acks=0`.
  - Schneller als `acks=all`, da keine Wartezeit auf Replikate.
- **Nachteile**:
  - Datenverlust möglich, wenn der Leader ausfällt, bevor die Nachricht auf die Replikate synchronisiert wurde.
- **Einsatzbereich**:
  - Für Anwendungen mit einem guten Kompromiss zwischen Geschwindigkeit und Zuverlässigkeit.

## `acks=all` (Bestätigung durch alle synchronen Replikate)

- **Funktionsweise**:
  - Der Producer wartet, bis die Nachricht nicht nur auf dem Leader, sondern auch auf allen synchronen Replikaten
    erfolgreich geschrieben wurde.
  - Synchron bedeutet, dass die Replikate aktuell und bereit sind.
- **Vorteile**:
  - Höchste Zuverlässigkeit: Nachrichten gehen nicht verloren, solange mindestens ein Broker einer Partition verfügbar
    ist.
  - Garantiert, dass die Nachricht auf mehreren Brokern persistiert ist.
- **Nachteile**:
  - Höhere Latenz, da der Producer auf Bestätigungen aller Replikate wartet.
  - Geringerer Durchsatz, besonders bei vielen Replikaten.
- **Einsatzbereich**:
  - Kritische Anwendungen, bei denen keine Datenverluste toleriert werden können (z. B. Finanztransaktionen).

## Zusammenfassung der Ack-Levels

| Ack-Typ    | Bestätigung                        | Vorteile                 | Nachteile                 | Einsatzbereich                            |
|------------|------------------------------------|--------------------------|---------------------------|-------------------------------------------|
| `acks=0`   | Keine                              | Maximale Geschwindigkeit | Potentieller Datenverlust | Unkritische Daten (z.B. Logs)             |
| `acks=1`   | Nur Leader                         | Guter Kompromiss         | Risiko bei Leader-Ausfall | Anwendungen mit moderater Zuverlässigkeit |
| `acks=all` | Leader + alle synchronen Replikate | Höchste Zuverlässigkeit  | Höhere Latenz             | Kritische und wichtige Daten              |

