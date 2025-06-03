package de.aschallenberg.botclient;

import de.aschallenberg.botclient.bot.Bot;
import de.aschallenberg.communication.dto.BotData;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Random;

@Slf4j
public class TicTacToeBot extends Bot {
    Random random = new Random();

    @Override
    public void onGameStart() {
        log.info("Started game: {}", getGameData().getName());
    }

    @Override
    public void onMoveReceived(Object moveObject) {
        Move move = jsonObjectMapper.convertValue(moveObject, Move.class);

        int[] board = move.getBoard();
        int index;

        do {
            index = random.nextInt(board.length);
        } while(board[index] != 0);

        sendMove(index);
    }

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
}
