package de.aschallenberg.botclient;

import de.aschallenberg.botclient.bot.Bot;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class TicTacToeBot extends Bot {
    @Override
    public void onGameStart() {
        log.info("Started game: {}", getGameName());
    }

    @Override
    public void onDataReceived(Map<String, Object> data) {
        String type = (String) data.get("type");

        if(data.containsKey("error")) {
            log.error("Error: {}", data.get("error"));
        }

        switch (type) {
            case "move_request" -> move((int[]) data.get("board"), (int) data.get("player"));
            case "game_over" -> gameOver(data);
            default -> log.warn("Unknown message type: {}", type);
        }

    }

    @Override
    public void onGameInterrupt() {
        log.info("Game was interrupted: {}", getGameName());
    }

    @Override
    public void onGameFinished() {
        log.info("Game finished: {}", getGameName());
    }

    private void gameOver(Map<String, Object> data) {
        String result = (String) data.get("result");

        if(result.equals("draw")) {
            log.info("Game ended in a draw");
        } else if(result.equals("win")) {
            if(data.get("winner").equals(getBotToken())) {
                log.info("You won the game");
            } else {
                log.info("You lost the game");
            }
        }

    }

    private void move(int[] board, int player) {
        int opponent = (player == 1) ? 2 : 1;

        // 1. Gewinnzug suchen
        int bestMove = findWinningMove(board, player);
        if (bestMove != -1) {
            sendData(Map.of("move_response", bestMove));
            return;
        }

        // 2. Gegnerischen Gewinn verhindern
        bestMove = findWinningMove(board, opponent);
        if (bestMove != -1) {
            sendData(Map.of("move_response", bestMove));
            return;
        }

        // 3. Zentrum bevorzugen
        if (board[4] == 0) {
            sendData(Map.of("move_response", 4));
            return;
        }

        // 4. Ecken bevorzugen
        int[] corners = {0, 2, 6, 8};
        for (int corner : corners) {
            if (board[corner] == 0) {
                sendData(Map.of("move_response", corner));
                return;
            }
        }

        // 5. Seiten oder erstes freies Feld nehmen
        for (int i = 0; i < 9; i++) {
            if (board[i] == 0) {
                sendData(Map.of("move_response", i));
                return;
            }
        }
    }

    // Hilfsmethode: Prüft, ob es einen Gewinnzug gibt
    private int findWinningMove(int[] board, int player) {
        int[][] winPatterns = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Reihen
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Spalten
                {0, 4, 8}, {2, 4, 6}             // Diagonalen
        };

        for (int[] pattern : winPatterns) {
            int count = 0, emptyIndex = -1;
            for (int index : pattern) {
                if (board[index] == player) count++;
                else if (board[index] == 0) emptyIndex = index;
            }
            if (count == 2 && emptyIndex != -1) {
                return emptyIndex;
            }
        }
        return -1;
    }
}
