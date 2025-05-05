package de.aschallenberg.botclient.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.aschallenberg.botclient.bot.Bot;
import de.aschallenberg.botclient.config.ConfigLoader;
import de.aschallenberg.botclient.bot.BotRegistry;
import de.aschallenberg.middleware.dto.BotData;
import de.aschallenberg.middleware.dto.GameData;
import de.aschallenberg.middleware.messages.Message;
import de.aschallenberg.middleware.messages.Payload;
import de.aschallenberg.middleware.messages.payloads.*;
import lombok.NonNull;
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
    private static final ObjectMapper mapper = new ObjectMapper();

    private final Bot bot = BotRegistry.instantiateBot();

    public WebSocketHandler(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        log.info("Connected to {}", getURI());

        String tokenAsString = ConfigLoader.get("platform.bot.token");
        UUID token = UUID.fromString(tokenAsString);

        MessageSender.sendMessage(new RegisterRequestPayload(token));
    }

    @Override
    public synchronized void onMessage(String messageString) {
        final Message message;
        try {
            message = mapper.readValue(messageString, Message.class);
        } catch (final JsonProcessingException e) {
            log.warn(PLATFORM_MARKER, "Could not parse message: {}", e.getMessage());
            MessageSender.sendMessage(new ErrorPayload("Invalid JSON format: " + e.getMessage()));
            return;
        }

        switch (message.getPayload()) {
            case final ErrorPayload payload -> handleError(message, payload);
            case final BotClientDisconnectPayload payload -> handleBotClientDisconnected(message, payload);
            case final RegisterRequestPayload payload -> ignore();
            case final RegisterResponsePayload payload -> handleRegisterResponse(message, payload);
            case final LogPayload payload -> ignore();
            case final LobbyJoinPayload payload -> ignore();
            case final LobbyStartPayload payload -> ignore();
            case final LobbyInterruptPayload payload -> handleInterrupt(message, payload);
            case final LobbyFinishedPayload payload -> ignore();
            case final GameStartForBotsPayload payload -> handleGameStart(message, payload);
            case final GameStartPayload payload -> ignore();
            case final GameInterruptPayload payload -> handleInterrupt(message, payload);
            case final GameFinishedPayload payload -> handleGameFinished(message, payload);
            case final StageStartPayload payload -> ignore();
            case final StageFinishedPayload payload -> handleStageFinished(message, payload);
            case final GameUpdatePayload<?> payload -> handleGameUpdate(message, payload);
            case final MovePayload<?> payload -> handleMove(message, payload);
            case final DisqualifyPayload payload -> handleDisqualify(message, payload);
            default -> handleUnknownMessage(message);
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

    private void handleError(
            @NonNull final Message message,
            @NonNull final ErrorPayload payload
    ) {
        bot.onError(payload.getErrorMessage());
    }

    private void handleBotClientDisconnected(
            @NonNull final Message message,
            @NonNull final BotClientDisconnectPayload payload
    ) {
        bot.onBotDisconnected(payload.getDisconnectedBot());
    }

    private void handleRegisterResponse(
            @NonNull final Message message,
            @NonNull final RegisterResponsePayload payload
    ) {
        log.info(PLATFORM_MARKER, "Successfully registered");

        // Join a lobby if a lobby code is given
        String lobbyJoinCodeString = ConfigLoader.get("platform.lobby.join.code");

        if(lobbyJoinCodeString != null && !lobbyJoinCodeString.isBlank()) {
            UUID lobbyJoinCode = UUID.fromString(lobbyJoinCodeString);

            MessageSender.sendMessage(new LobbyJoinPayload(lobbyJoinCode));
        }
    }

    private void handleGameStart(
            @NonNull final Message message,
            @NonNull final GameStartForBotsPayload payload
    ) {
        bot.onGameStart(payload.getGameData(), payload.getOwnBotData());
    }

    private void handleInterrupt(
            @NonNull final Message message,
            @NonNull final Payload payload
    ) {
        bot.onGameInterrupt();
    }

    private void handleGameFinished(
            @NonNull final Message message,
            @NonNull final GameFinishedPayload payload
    ) {
        bot.onGameFinished(payload.getScoresByBots());
    }

    private void handleStageFinished(
            @NonNull final Message message,
            @NonNull final StageFinishedPayload payload
    ) {
        if(payload.isQualifiedForNextRound()) {
            log.info("Stage has finished. Congratulations! You’ve made it to the next round!");
        } else {
            log.info("Stage has finished. Unfortunately, you didn’t make it. Maybe next time.");
        }
    }

    private void handleGameUpdate(
            @NonNull final Message message,
            @NonNull final GameUpdatePayload<?> payload
    ) {
        bot.onGameUpdateReceived(payload.getValue());
    }

    private void handleMove(
            @NonNull final Message message,
            @NonNull final MovePayload<?> payload
    ) {
        bot.onMoveReceived(payload.getValue());
    }

    private void handleDisqualify(
            @NonNull final Message message,
            @NonNull final DisqualifyPayload payload
    ) {
        bot.onDisqualify(payload.getDisqualifiedBot());
    }

    private void handleUnknownMessage(@NonNull final Message message) {
        bot.onOtherMessageReceived(message.getPayload());
    }

    private void ignore() {}
}
