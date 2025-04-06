# Platform Bot Library

## Einbinden
Füge folgende dependency in die pom.xml deines Maven Projekts hinzu:
```xml
<dependency>
  <groupId>de.aschallenberg</groupId>
  <artifactId>platform-bot-client</artifactId>
  <version>1.0.0</version>
</dependency>
```

Beachte, dass die Library Java 21 benutzt.

## Anfangen den Bot zu schreiben

Du brauchst primär drei Dinge um einen Bot zu schreiben: eine main-Methode, eine (Haupt-)Klasse für deinen Bot und eine
config-Datei.

## config-Datei
Die config Datei musst du als `config.properties` in `src/main/resources` anlegen. Sie muss folgendes beinhalten:
```
platform.host=<IP-Adresse der Platform zu der du dich verbinden möchtest>
platform.port=<Port der Plattform zu der du dich verbinden möchtest>
platform.bot.token=<Der Bot-Token, den du von der Bot-Registrierung auf der Plattform bekommst>
```

## (Haupt-)Klasse
Du musst eine Klasse für deinen Bot anlegen, die von `de.aschallenberg.botclient.bot.Bot` erben muss. Du wirst einige
Methoden implementieren müssen. Sie sind die Schnittstelle zur Plattform und zum Spiel was du spielen möchtest.

## main-Methode
Dein Programm braucht eine main-Methode. Ich empfehle, diese in einer eigenen Klasse `Main` anzulegen, du kannst sie
aber auch überall anders implementieren. Wichtig ist, dass du deine Hauptklasse registrierst. Das machst du mit
`BotRegistry.setBotClass(<Deine Hauptklasse>.class)`. **Danach** musst du die Library dazu auffordern, eine WebSocket-
Verbindung zur Plattform aufzubauen. Dies funktioniert mit dem Befehl `WebSocketInitiator.initConnection()`.
Deine main- Methode sollte jetzt in etwa so aussehen:
```java
public static void main(String[] args) {
	BotRegistry.setBotClass(<Deine Hauptklasse>.class);
	WebSocketInitiator.initConnection();
}
```


Wenn du alle drei Komponenten hast und korrekt konfiguriert hast, dann sollte der Bot beim Starten eine WebSocket-
Verbindung zur Plattform aufbauen und sich dort registrieren. Du solltest dann in der Konsole etwas sehen wie
"Successfully registered". Der Bot ist nun online und du kannst mit ihm theoretisch in Lobbies beitreten und die 
vorgesehenen Spiele spielen. Jedoch wird das zu Fehlern führen, da du ihn bis jetzt noch nicht implementiert hast.

## Implementierung
Die vom `Bot` überschriebenen Methoden musst du jetzt implementieren. Je nach Spiel musst du mehr oder weniger Methoden
implementieren. Den Rest kannst du einfach leer lassen. Bitte beachte genau auf die Implementierungsangaben des Spiels.
Ein falscher Typ der gesendet wird, kann im schlimmsten Fall zum Spielabbruch führen.