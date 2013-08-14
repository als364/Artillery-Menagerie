package edu.cornell.slicktest;

import java.util.ArrayList;


public class BattleJoinResult {
	public String battleKey;
	public int playerNumber;
	public ArrayList<Army> armies;
	public boolean isFirstTurn;
	public BattleJoinResult(String battleKey, int player, ArrayList<Army> armies) {
		this.battleKey = battleKey;
		this.playerNumber = player;
		this.armies = armies;
		isFirstTurn = true;
	}
	
	public BattleJoinResult(String battleKey, int player, ArrayList<Army> armies, boolean isFirstTurn) {
		this.battleKey = battleKey;
		this.playerNumber = player;
		this.armies = armies;
		this.isFirstTurn = isFirstTurn;
	}
	
}
