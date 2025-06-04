
# Platform Bot Library

## Einbinden  
Fügen Sie folgende Dependency in die `pom.xml` Ihres Maven-Projekts ein:

```xml
<dependency>
  <groupId>de.aschallenberg</groupId>
  <artifactId>platform-bot-client</artifactId>
  <version>1.0.0</version>
</dependency>
```

Beachten Sie, dass die Library Java 21 verwendet.

## Anfangen, den Bot zu schreiben

Sie benötigen primär drei Dinge, um einen Bot zu schreiben: eine `main`-Methode, eine (Haupt-)Klasse für Ihren Bot und eine Konfigurationsdatei.

## Konfigurationsdatei  
Die Konfigurationsdatei müssen Sie als `config.properties` in `src/main/resources` anlegen. Sie muss Folgendes beinhalten:

```
platform.host=<IP-Adresse der Plattform, zu der Sie sich verbinden möchten>
platform.port=<Port der Plattform, zu der Sie sich verbinden möchten>
platform.bot.token=<Der Bot-Token, den Sie von der Bot-Registrierung auf der Plattform erhalten>
```

## (Haupt-)Klasse  
Sie müssen eine Klasse für Ihren Bot anlegen, die von `de.aschallenberg.botclient.bot.Bot` erbt.  
Sie werden einige Methoden implementieren müssen. Diese sind die Schnittstelle zur Plattform und zu dem Spiel, das Sie spielen möchten.

## Main-Methode  
Ihr Programm benötigt eine `main`-Methode. Es wird empfohlen, diese in einer eigenen Klasse `Main` anzulegen. Sie können sie jedoch auch an anderer Stelle implementieren. Zunächst muss die Konfigurations-Datei geladen werden. Dies geschieht mit

```java
ConfigLoader.load(args);
```
Wichtig ist auch, dass Sie Ihre Hauptklasse registrieren. Das machen Sie mit:

```java
BotRegistry.setBotClass(<Ihre Hauptklasse>.class);
```

**Danach** müssen Sie die Library dazu auffordern, eine WebSocket-Verbindung zur Plattform aufzubauen. Dies funktioniert mit dem Befehl:

```java
WebSocketInitiator.initConnection();
```

Ihre `main`-Methode sollte jetzt in etwa so aussehen:

```java
public static void main(String[] args) {
    ConfigLoader.load(args);
    BotRegistry.setBotClass(<Ihre Hauptklasse>.class);
    WebSocketInitiator.initConnection();
}
```

Wenn Sie alle drei Komponenten haben und diese korrekt konfiguriert sind, sollte der Bot beim Starten eine WebSocket-Verbindung zur Plattform aufbauen und sich dort registrieren. Sie sollten dann in der Konsole etwas sehen wie  
„Successfully registered“. Der Bot ist nun online und Sie können mit ihm theoretisch Lobbies beitreten und die vorgesehenen Spiele spielen.  
Jedoch wird das zu Fehlern führen, da Sie ihn bis jetzt noch nicht implementiert haben.

## Implementierung  
Die vom `Bot` überschriebenen Methoden müssen Sie nun implementieren. Je nach Spiel müssen Sie mehr oder weniger Methoden implementieren. Den Rest können Sie einfach leer lassen.  
Bitte beachten Sie genau die Implementierungsangaben des Spiels. Ein falscher Typ, der gesendet wird, kann im schlimmsten Fall zum Spielabbruch führen.
