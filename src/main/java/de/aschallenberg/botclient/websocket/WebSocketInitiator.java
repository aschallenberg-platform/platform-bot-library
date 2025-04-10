package de.aschallenberg.botclient.websocket;

import de.aschallenberg.botclient.config.ConfigLoader;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import java.net.URI;

@Log4j2
@UtilityClass
public class WebSocketInitiator {

    /**
     * The URI format for the WebSocket connection.
     * <p>
     * This format is used to create the WebSocket URI using the host and port
     * specified in the configuration.
     * </p>
     */
    private static final String WS_ENDPOINT = "/ws/bot";

    /**
     * Initializes the WebSocket connection.
     * <p>
     * This method retrieves the WebSocket host and port from the configuration,
     * creates a WebSocketHandler client, and attempts to establish a connection
     * synchronously. If the connection attempt is interrupted, it logs a warning
     * message and re-interrupts the current thread.
     * </p>
     */
    public static void initConnection()  {
        initConnection(true);
    }

    public static void initConnection(boolean interruptOnFailure) {
        ConfigLoader.load();

        String host = ConfigLoader.get("platform.host");
        String port = ConfigLoader.get("platform.port");

        WebSocketHandler client = new WebSocketHandler(getWsUri());
        MessageSender.setWebSocketHandler(client);

        try {
            // Establish connection synchronously
            client.connectBlocking();
        } catch (InterruptedException e) {
            if(interruptOnFailure) {
                log.warn(e.getMessage());
                System.exit(1);
            } else {
                log.warn("Cannot connect to the platform");
            }
        }
    }

    private URI getWsUri() {
        String host = ConfigLoader.get("platform.host");
        String port = ConfigLoader.get("platform.port");
        boolean ssl = Boolean.parseBoolean(ConfigLoader.get("platform.ssl"));

        StringBuilder uriStringBuilder = new StringBuilder();

        uriStringBuilder
                .append(ssl ? "wss" : "ws")
                .append("://")
                .append(host);

        if(port != null && !port.isBlank()) {
            uriStringBuilder
                    .append(":")
                    .append(port);
        }

        uriStringBuilder
                .append(WS_ENDPOINT);

        System.out.println(uriStringBuilder);

        return URI.create(uriStringBuilder.toString());
    }
}
