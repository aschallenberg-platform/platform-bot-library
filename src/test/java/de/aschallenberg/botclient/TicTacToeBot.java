package de.aschallenberg.botclient;

import de.aschallenberg.botclient.bot.Bot;
import de.aschallenberg.botclient.data.BotData;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Map;

@Slf4j
public class TicTacToeBot extends Bot {
    @Override
    public void onGameStart() {
        log.info("Started game: {}", getGameData().getName());
    }

    @Override
    public void onMove(Object object) {
        Move move = jsonObjectMapper.convertValue(object, Move.class);

        int[] board = move.getBoard();
        int player = move.getPlayer();
        int opponent = (move.getPlayer() == 1) ? 2 : 1;

        // 1. Gewinnzug suchen
        int bestMove = findWinningMove(board, player);
        if (bestMove != -1) {
            sendMove(bestMove);
            return;
        }

        // 2. Gegnerischen Gewinn verhindern
        bestMove = findWinningMove(board, opponent);
        if (bestMove != -1) {
            sendMove(bestMove);
            return;
        }

        // 3. Zentrum bevorzugen
        if (board[4] == 0) {
            sendMove(4);
            return;
        }

        // 4. Ecken bevorzugen
        int[] corners = {0, 2, 6, 8};
        for (int corner : corners) {
            if (board[corner] == 0) {
                sendMove(corner);
                return;
            }
        }

        // 5. Seiten oder erstes freies Feld nehmen
        for (int i = 0; i < 9; i++) {
            if (board[i] == 0) {
                sendMove(i);
                return;
            }
        }
    }

    @Override
    public void onMessageReceived(Object object) {

    }

    @Override
    public void onGameInterrupt() {}

    @Override
    public void onGameFinished(Map<BotData, Integer> scores) {
        log.info("Game finished");

        int myScore = scores.get(getMyBotData());
        switch (myScore) {
            case 0 -> log.info("Lost the game! :(");
            case 1 -> log.info("It's a draw! :|");
            case 2 -> log.info("Won the game! :)");
        }
    }

    @Override
    public void resetBot() {}

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
