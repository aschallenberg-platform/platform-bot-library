package de.aschallenberg.botclient.websocket;

public enum MessageType {
    ERROR,
    BOT_CLIENT_DISCONNECTED,
    REGISTER,
    START,
    INTERRUPT,
    FINISHED,
    GAME_INTERNAL,
    MOVE
}
