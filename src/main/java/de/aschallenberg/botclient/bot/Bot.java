package de.aschallenberg.botclient.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.aschallenberg.botclient.config.ConfigLoader;
import de.aschallenberg.botclient.websocket.MessageSender;
import lombok.*;

import java.util.Map;
import java.util.UUID;


public abstract class Bot {
    protected final ObjectMapper jsonObjectMapper = new ObjectMapper();

    @Getter
    private UUID botToken;

    @Getter
    private String gameName;

    @Getter
    private String gameModuleName;

    @Getter
    private String gameVersion;

    @Getter
    private Map<String, Object> gameSettings;

    public final void onGameStart(String gameName, String gameModuleName, String gameVersion, Map<String, Object> gameSettings) {
        this.botToken = UUID.fromString(ConfigLoader.get("plattform.bot.token"));
        this.gameName = gameName;
        this.gameModuleName = gameModuleName;
        this.gameVersion = gameVersion;
        this.gameSettings = gameSettings;
        onGameStart();
    }

    public abstract void onGameStart();

    public abstract  void onDataReceived(Map<String, Object> data);

    public abstract void onGameInterrupt();

    public abstract void onGameFinished();

    protected final void sendData(@NonNull Map<String, Object> data) {
        MessageSender.sendGameData(data);
    }
}
