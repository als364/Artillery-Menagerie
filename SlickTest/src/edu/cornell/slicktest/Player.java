package edu.cornell.slicktest;

import java.util.ArrayList;

public class Player {
	
	static Player player = null;
	
	protected int money;
	protected ArrayList<Unit> units;
	protected ArrayList<Item> items;
	protected int rank;
	
	private Player() {
		
	}
	
	public Player getInstance() {
		if (player == null) {
			player = new Player();
		}
		return player;
	}
	
}