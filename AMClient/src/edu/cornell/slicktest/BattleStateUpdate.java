package edu.cornell.slicktest;

import org.json.JSONArray;

public class BattleStateUpdate {
	public boolean needUpdate;
	public int turnCount;
	public boolean battleEnded;
	public int winner;
	public JSONArray framesToBeAnimated;
	
	public BattleStateUpdate() {
	}
}
