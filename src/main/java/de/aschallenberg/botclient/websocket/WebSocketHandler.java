package de.aschallenberg.botclient.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.aschallenberg.botclient.bot.Bot;
import de.aschallenberg.botclient.bot.BotRegistry;
import lombok.extern.log4j.Log4j2;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.*;

@Log4j2
public final class WebSocketHandler extends WebSocketClient {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Bot bot = BotRegistry.instantiateBot();

    public WebSocketHandler(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        log.info("Connected to {}", getURI());

        MessageSender.sendPlatformData(MessageType.REGISTER, null);
    }

    @Override
    public synchronized void onMessage(String message) {
        Map<String, Object> data = objectMapper.convertValue(message, Map.class);
        MessageType type = MessageType.valueOf((String) data.get(MessageSender.TYPE_KEY));

        log.info("Message received: {}", message);

        switch (type) {
            case REGISTER -> log.info("Successfully registered");
            case START -> handleGameStart(data);
            case INTERRUPT -> bot.onGameInterrupt();
            case FINISHED -> bot.onGameFinished();
            default -> {
                Map<String, Object> gameData = objectMapper.convertValue(data.get(MessageSender.GAME_DATA_KEY), Map.class);
                bot.onDataReceived(gameData);
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.warn("Connection closed ({}): {}", code, reason);
    }

    @Override
    public void onError(Exception ex) {
        log.error(ex.getMessage());
    }

    private void handleGameStart(Map<String, Object> data) {
        String game = (String) data.get("game");
        String module = (String) data.get("module");
        String version = (String) data.get("version");
        Map<String, Object> settings = (Map<String, Object>) data.get("settings");

        bot.onGameStart(game, module, version, settings);
    }
}
