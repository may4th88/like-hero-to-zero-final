# Like Hero To Zero

Diese Webanwendung wurde im Rahmen der Fallstudie im Modul „Programmierung von industriellen Informationssystemen“ erstellt. Sie stellt CO₂-Emissionsdaten öffentlich dar und bietet ein geschütztes Backend zur Pflege und Validierung von Daten.

## Verwendete Technologien

- Java
- Jakarta Faces
- CDI / Weld
- Jakarta Persistence API
- Hibernate
- PrimeFaces
- MySQL
- Apache Tomcat 10.1
- Maven

## Voraussetzungen

- Java 17 oder höher
- Maven
- Apache Tomcat 10.1
- MySQL Server
- Git

## Datenbank einrichten

Vor dem Start der Anwendung muss eine leere MySQL-Datenbank mit dem Namen `like_hero_to_zero` vorhanden sein.

```sql
CREATE DATABASE like_hero_to_zero
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

Zusätzlich wird ein Datenbankbenutzer benötigt, der in der Datei `persistence.xml` verwendet wird:

```sql
CREATE USER 'lhz_user'@'%' IDENTIFIED BY 'lhz_password';
GRANT ALL PRIVILEGES ON like_hero_to_zero.* TO 'lhz_user'@'%';
FLUSH PRIVILEGES;
```

## Automatische Initialisierung der Datenbank

Beim Start der Anwendung erzeugt Hibernate die Tabellen automatisch. Anschließend wird die Datei

```text
src/main/resources/import.sql
```

ausgeführt. Diese Datei enthält die Initialdaten für Länder, Benutzer und Emissionswerte.

Die Emissionsdaten stammen aus einem OWID-basierten CO₂-Datensatz (liegt im Repository vor). Für die Anwendung wurden je Land die letzten zehn verfügbaren Jahreswerte übernommen. Die Werte werden in Millionen Tonnen CO₂ gespeichert.

## Anwendung starten

1. Repository klonen.
2. Projekt als Maven-Projekt in Eclipse oder einer anderen IDE importieren.
3. MySQL-Datenbank und Benutzer wie oben beschrieben anlegen.
4. Projekt auf Apache Tomcat 10.1 deployen.
5. Anwendung im Browser öffnen:

```text
http://localhost:8080/webapplikation/
```

Der konkrete Kontextpfad kann je nach lokaler Tomcat-Konfiguration abweichen.

## Testzugänge

Wissenschaftler:

```text
Benutzername: scientist
Passwort: scientist
```

Herausgeber:

```text
Benutzername: editor
Passwort: editor
```

## Rollen

- Wissenschaftler können neue Emissionsdaten anlegen oder bestehende Werte zur Änderung vormerken.
- Herausgeber können vorgemerkte Änderungen validieren.

## Hinweis

Die Anwendung ist für die lokale Ausführung im Rahmen der Fallstudie vorbereitet. Die Datenbank wird beim Start anhand der JPA-Entities neu erzeugt und mit Beispieldaten befüllt.
