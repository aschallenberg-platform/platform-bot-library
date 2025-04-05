package de.aschallenberg.botclient.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.aschallenberg.botclient.config.ConfigLoader;
import de.aschallenberg.botclient.data.BotData;
import de.aschallenberg.botclient.data.GameData;
import de.aschallenberg.botclient.websocket.MessageSender;
import de.aschallenberg.botclient.websocket.MessageType;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.UUID;


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
    public final void onGameStart(GameData gameData) {
        UUID myToken = UUID.fromString(ConfigLoader.get("platform.bot.token"));

        this.gameData = gameData;
        this.myBotData = gameData.getBots()
                .stream()
                .filter(bot -> bot.getToken().equals(myToken))
                .findFirst()
                .orElseThrow();

        onGameStart();
    }

    /**
     * Abstract method to be implemented by subclasses to define behavior when the game starts.
     */
    public abstract void onGameStart();

    /**
     * Abstract method to be implemented by subclasses to define behavior when a move is made.
     *
     * @param object The object representing the move.
     */
    public abstract void onMove(Object object);

    /**
     * Abstract method to be implemented by subclasses to define behavior when a message is received.
     *
     * @param object The object representing data.
     */
    public abstract void onMessageReceived(Object object);

    /**
     * Abstract method to be implemented by subclasses to define behavior when the game finishes.
     */
    public abstract void onGameFinished(Map<BotData, Object> scores);

    /**
     * Logs an error to the console. Can be overridden to handle specific errors.
     */
    public void onError(String error) {
        log.error(error);
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
     * Sends a move message to the game
     * <p>
     * This method sends a message of type MOVE to the platform, including the specified object. The platform will
     * forward it to the game.
     * </p>
     *
     * @param object The object representing the move.
     */
    protected void sendMove(Object object) {
        MessageSender.sendMessage(MessageType.MOVE, object, myBotData);
    }

    /**
     * Sends a message to the game.
     * <p>
     * This method sends a message of type GAME_INTERNAL to the platform, including the specified object. The platform
     * will forward it to the game.
     * It also logs the message if the logMessage parameter is true. Please check the game flow in the game details
     * on the platform.
     * </p>
     *
     * @param object     The object to be sent in the message.
     */
    protected final void sendMessage(@NonNull Object object) {
        MessageSender.sendMessage(MessageType.GAME_INTERNAL, object, myBotData);
    }
}
