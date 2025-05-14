package de.aschallenberg.botclient.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.aschallenberg.botclient.websocket.MessageSender;
import de.aschallenberg.middleware.dto.BotData;
import de.aschallenberg.middleware.dto.GameData;
import de.aschallenberg.middleware.messages.Payload;
import de.aschallenberg.middleware.messages.payloads.GameUpdatePayload;
import de.aschallenberg.middleware.messages.payloads.MovePayload;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.util.Map;


@Log4j2
public abstract class Bot {
    /**
     * JSON object mapper used for converting objects to and from JSON for sending them to the platform and the game.
     */
    protected final ObjectMapper jsonObjectMapper = new ObjectMapper();

    /**
     * The game data associated with the current game.
     */
    @Getter
    private GameData gameData;

    /**
     * The bot data for this bot instance.
     */
    @Getter
    private BotData myBotData;

    /**
     * Called when the game starts. Initializes the bot with the game data and identifies the bot using a token.
     *
     * @param gameData The data associated with the current game.
     */
    public final void onGameStart(GameData gameData, BotData myBotData) {
        this.gameData = gameData;
        this.myBotData = myBotData;

        onGameStart();
    }

    /**
     * Abstract method to be implemented by subclasses to define behavior when the game starts.
     */
    public abstract void onGameStart();

    /**
     * Abstract method to be implemented by subclasses to define behavior when a move is made.
     *
     * @param move The move.
     */
    public abstract void onMoveReceived(final Object move);

    /**
     * Called when the platform forwards an update message from the game to this bot. This method is used to handle any
     * incoming data for actions that are not a move or start.
     * Please look up if you need this method in the game description on the platform.
     *
     * @param gameUpdateData The game update data.
     */
    public void onGameUpdateReceived(final Object gameUpdateData) {}

    /**
     * Handles the reception of self-created messages from the game.
     * <p>
     * Self-created messages are described in the game description on the platform. Only use this method if the game
     * defines it in its description.
     * </p>
     *
     * @param payload The payload of the unknown message.
     */
    public void onOtherMessageReceived(final Payload payload) {}

    /**
     * Abstract method to be implemented by subclasses to define behavior when the game finishes.
     */
    public abstract void onGameFinished(Map<BotData, Integer> scores);

    /**
     * Logs an error to the console. Can be overridden to handle specific errors.
     */
    public void onError(String error) {
        log.error(error);
        System.exit(2);
    }

    /**
     * Abstract method to be implemented by subclasses to reset the bot's state.
     */
    public abstract void resetBot();

    /**
     * Called when the game is interrupted. Resets the bot's state.
     */
    public void onGameInterrupt() {
        resetBot();
    }

    /**
     * This method may be called when another participating bot disconnects.
     * It does not need to be overridden and does nothing by default.
     * Only override it if the game explicitly specifies how to handle the disconnection of another participating bot.
     *
     * @param bot The participating bot that disconnected
     */
    public void onBotDisconnected(BotData bot) {
    }

    /**
     * Handles the disqualification of a bot.
     * <p>
     * If the current bot is disqualified, it resets the bot's state and logs an error message.
     * Otherwise, it logs an informational message about the disqualified bot.
     * </p>
     * Can be overwritten for handle a disqualification more specifically.
     *
     * @param bot The bot that was disqualified.
     */
    public void onDisqualify(BotData bot) {
        if(bot.equals(myBotData)) {
            resetBot();
            log.error("I was disqualified");
        } else {
            log.info("Bot {} from {} was disqualified", bot.getName(), bot.getOwnerName());
        }
    }

    /**
     * Sends a move message to the game
     * <p>
     * This method sends a message of type MOVE to the platform, including the specified object. The platform will
     * forward it to the game.
     * </p>
     *
     * @param value The value representing the move.
     */
    protected final void sendMove(Object value) {
        MessageSender.sendMessage(new MovePayload<>(value));
    }

    /**
     * Sends a game update to the game.
     * <p>
     * This method sends a game update message to the game (via the platform), including the specified object.
     * Please check the game flow in the game details on the platform.
     * </p>
     *
     * @param object The object to be sent in the message.
     */
    protected final void sendGameUpdate(@NonNull Object object) {
        MessageSender.sendMessage(new GameUpdatePayload<>(object));
    }

    /**
     * Sends a self-created message to the game.
     * <p>
     * This method sends a self-created message to the game (via the platform), including the specified object. The platform
     * will forward it to the game.
     * It also logs the message if the logMessage parameter is true. Please check the game flow in the game details
     * on the platform.
     * </p>
     *
     * @param payload The object to be sent in the message.
     */
    protected final void sendOtherMessage(@NonNull Payload payload) {
        MessageSender.sendMessage(payload);
    }
}
