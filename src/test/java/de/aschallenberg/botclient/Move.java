package de.aschallenberg.botclient;

import lombok.Data;

import java.io.Serializable;

@Data
public class Move implements Serializable {
	private int[] board;
	private int player;
}
