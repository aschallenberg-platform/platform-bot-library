package de.aschallenberg.botclient.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.aschallenberg.botclient.config.ConfigLoader;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@UtilityClass
public class MessageSender {
    static final String BOT_TOKEN_KEY = "bot_token";
    static final String TYPE_KEY = "type";
    static final String GAME_DATA_KEY = "game_data";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static WebSocketHandler webSocketHandler;

    public static void sendGameData(@NonNull Map<String, Object> data) {
        sendData(MessageType.GAME_INTERNAL, data);
    }

    static void sendPlatformData(@NonNull MessageType type, Map<String, Object> data) {
        sendData(type, data);
    }

    static void sendData(MessageType type, Map<String, Object> gameData) {
        Map<String, Object> data = new HashMap<>();

        data.put(TYPE_KEY, type.name());
        data.put(BOT_TOKEN_KEY, ConfigLoader.get("plattform.bot.token"));

        if (type == MessageType.GAME_INTERNAL) {
            data.put(GAME_DATA_KEY, gameData);
        }

        String dataString;
        try {
            dataString = OBJECT_MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("'data' could not be converted to JSON: " + e.getMessage());
        }

        log.info("Sending data: {}", dataString);
        webSocketHandler.send(dataString);
    }

    static void setWebSocketHandler(WebSocketHandler webSocketHandler) {
        MessageSender.webSocketHandler = webSocketHandler;
    }
}
