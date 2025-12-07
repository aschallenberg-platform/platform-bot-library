package de.aschallenberg.botclient;

import lombok.Data;

import java.io.Serializable;

@Data
public class Move implements Serializable {
	private int[] board;
	private int player;

	public int[] getBoard() {
		return board;
	}

	public void setBoard(final int[] board) {
		this.board = board;
	}

	public int getPlayer() {
		return player;
	}

	public void setPlayer(final int player) {
		this.player = player;
	}
}
