package de.aschallenberg.botclient.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.aschallenberg.botclient.bot.Bot;
import de.aschallenberg.botclient.config.ConfigLoader;
import de.aschallenberg.botclient.data.BotData;
import de.aschallenberg.botclient.bot.BotRegistry;
import de.aschallenberg.botclient.data.BotDataKeyDeserializer;
import de.aschallenberg.botclient.data.GameData;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.*;

@Log4j2
public final class WebSocketHandler extends WebSocketClient {
    private static final Marker PLATFORM_MARKER = MarkerManager.getMarker("Platform");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        addMapperModules();
    }

    private final Bot bot = BotRegistry.instantiateBot();

    public WebSocketHandler(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        log.info("Connected to {}", getURI());

        MessageSender.sendMessage(MessageType.REGISTER, ConfigLoader.get("platform.bot.token"));
    }

    @Override
    public synchronized void onMessage(String message) {
        log.info("DEBUG: Received {}", message);

        Map<String, Object> data;
        try {
            data = OBJECT_MAPPER.readValue(message, Map.class);
        } catch (JsonProcessingException e) {
            log.error("Can't parse message: {}", e.getMessage());
            return;
        }
        MessageType type = MessageType.valueOf((String) data.get(MessageSender.TYPE_KEY));
        Object object = data.get(MessageSender.OBJECT_KEY); // null if the map doesn't have an object

        log.info("Received [{}]: {}", type, message);

        switch (type) {
            case ERROR -> bot.onError(OBJECT_MAPPER.convertValue(object, String.class));
            case BOT_CLIENT_DISCONNECTED -> bot.onBotDisconnected(OBJECT_MAPPER.convertValue(object, BotData.class));
            case REGISTER -> log.info(PLATFORM_MARKER, "Successfully registered");
            case START -> handleStart(object);
            case INTERRUPT -> bot.onGameInterrupt();
            case FINISHED -> bot.onGameFinished(OBJECT_MAPPER.convertValue(object, new TypeReference<>() {}));
            case GAME_INTERNAL -> bot.onMessageReceived(object);
            case MOVE -> bot.onMove(object);
            case DISQUALIFY -> bot.onDisqualify(OBJECT_MAPPER.convertValue(object, BotData.class));
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

    private static void addMapperModules() {
        SimpleModule module = new SimpleModule();

        module.addKeyDeserializer(BotData.class, new BotDataKeyDeserializer());

        OBJECT_MAPPER.registerModule(module);
    }

    private void handleStart(Object object) {
        HashMap<String, Object> data = OBJECT_MAPPER.convertValue(object, new TypeReference<>(){});

        bot.onGameStart(
                OBJECT_MAPPER.convertValue(data.get("gameData"), GameData.class),
                OBJECT_MAPPER.convertValue(data.get("botData"), BotData.class)
        );
    }
}
