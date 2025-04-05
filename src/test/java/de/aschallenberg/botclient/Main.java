package de.aschallenberg.botclient;

import de.aschallenberg.botclient.bot.BotRegistry;
import de.aschallenberg.botclient.config.ConfigLoader;
import de.aschallenberg.botclient.websocket.WebSocketInitiator;

public class Main {
    public static void main(String[] args) {
        BotRegistry.setBotClass(TicTacToeBot.class);
        WebSocketInitiator.initConnection();
    }
}
