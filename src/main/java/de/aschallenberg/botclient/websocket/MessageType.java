package de.aschallenberg.botclient.websocket;

public enum MessageType {
    ERROR,
    BOT_CLIENT_DISCONNECTED,
    REGISTER,
    LOBBY_START,
    GAME_START,
    LOBBY_INTERRUPT,
    GAME_INTERRUPT,
    LOBBY_FINISHED,
    GAME_FINISHED,
    GAME_INTERNAL,
    MOVE,
    DISQUALIFY
}
