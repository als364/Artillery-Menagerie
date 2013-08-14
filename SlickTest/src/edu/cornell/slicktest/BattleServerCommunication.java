package edu.cornell.slicktest;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import org.jbox2d.common.Vec2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.newdawn.slick.geom.Rectangle;

public class BattleServerCommunication {

	public int loadWaitCount;
	public final int maxLoadWaitCount = 500;
	private JSONArray frameList;
	
	public BattleServerCommunication() {
		frameList = new JSONArray();
		loadWaitCount = 0;
	}
	
	public BattleJoinResult startBattle(String player) throws IOException, JSONException, InterruptedException {
		//System.out.println(player);
		String battleKey = ServerConnection.getBattle();
		int count = 0;
		while (true) {
			JSONObject join = ServerConnection.joinBattle(battleKey, player);
			if (join.getBoolean("battleFound")) {
				if (join.getBoolean("joined")) {
					int playerNumber = join.getInt("number");
					System.out.println("Join: " + join);
					return new BattleJoinResult(battleKey, playerNumber);
				}
				else {
					count++;
					battleKey = ServerConnection.getBattle(count);
				}
			}
			else {
				Thread.sleep(250);
			}
		}
	}
	
	public void storeStateToServer(String battleKey, boolean battleEnded, int turn, int playerNumber, ArrayList<Army> armies) throws JSONException, IOException {
		JSONObject object = new JSONObject();
		object.put("battleKey", battleKey);
		object.put("finished", battleEnded);
		object.put("turn", turn);
		object.put("player", playerNumber);
		object.put("frames", frameList);
		
		//System.out.println("Store: " + object);
		ServerConnection.storeBattle(object);
	}
	
	public BattleStateUpdate loadStateFromServer(String battleKey, int turn, ArrayList<Army> armies, 
			ArrayList<ArrayList<UnitAnimation>> unitAnimations, Physics physics, MSTerrain _terrain) throws IOException, JSONException, InterruptedException {
		
		JSONObject object = ServerConnection.loadBattle(battleKey, turn);
		
		//System.out.println("Load: " + object);
		
		BattleStateUpdate update = new BattleStateUpdate();
		if (object.getBoolean("needUpdate")) {
			String battleStateStr = object.getString("battleState");
			JSONObject battleState = new JSONObject(battleStateStr);
			update.turnCount = battleState.getInt("turn");
			update.battleEnded = battleState.getBoolean("finished");
			
			JSONArray frameArray = battleState.getJSONArray("frames");
			
			update.framesToBeAnimated = frameArray;
			update.needUpdate = true;
		}
		else {
			update.needUpdate = false;
		}
		return update;
	}
	
	public boolean waitOnLoad(int delta) {
		loadWaitCount += delta;
		if (loadWaitCount > maxLoadWaitCount) {
			loadWaitCount = 0;
			return false;
		}
		else {
			return true;
		}
	}
	
	public void addExplosion(int radius, int xpos, int ypos) throws JSONException {
		JSONObject object = new JSONObject();
		object.put("radius", radius);
		object.put("xpos", xpos);
		object.put("ypos", ypos);
		if (frameList.length() != 0) { 
			JSONObject latestFrame = frameList.getJSONObject(frameList.length()-1);
			latestFrame.put("explosion", object);
		}
	}
	
	public void addShot(Unit shooter, int skillNumber) throws JSONException {
		JSONObject object = new JSONObject();
		object.put("unitID", shooter.displayName);
		object.put("skillNumber", skillNumber);
		if (frameList.length() != 0) { 
			JSONObject latestFrame = frameList.getJSONObject(frameList.length()-1);
			latestFrame.put("shot", object);
		}
	}
	
	public void readyForNewTurn() {
		while(frameList.length() != 0) {
			frameList.remove(0);
		}
	}
	
	public void storeFrame(ArrayList<Army> armies, Physics physics) throws JSONException {
		JSONObject object = new JSONObject();
		
		// Store unit data of the current frame
		JSONArray units = new JSONArray();
		for (Army army : armies) {
			for (Unit unit : army.units) {
				units.put(unit.createJSONStateObject());
			}
		}
		object.put("units", units);
		
		// Store projectile point
		Point projectilePoint = physics.getProjectilePositionPoint();
		if (projectilePoint != null) {
			JSONObject projectile = new JSONObject();
			projectile.put("xpos", projectilePoint.getX());
			projectile.put("ypos", projectilePoint.getY());
			object.put("projectile", projectile);
		}
		
		if (!sameAsPreviousFrame(object)) {
			frameList.put(object);
		}
	}
	
	private boolean sameAsPreviousFrame(JSONObject frame) throws JSONException {
		if (frameList.length() == 0) {
			return false;
		}
		JSONObject latestFrame = frameList.getJSONObject(frameList.length()-1);
		
		// Check units
		
		JSONArray units1 = frame.getJSONArray("units");
		JSONArray units2 = latestFrame.getJSONArray("units");
		if (units1.length() != units2.length()) {
			return false;
		}
		int unitsLength = units1.length();
		for (int i = 0; i < unitsLength; i++) {
			JSONObject unit1 = units1.getJSONObject(i);
			JSONObject unit2 = units2.getJSONObject(i);
			if (!unit1.getString("unitID").equals(unit2.getString("unitID"))) {
				return false;
			}
			if (unit1.getInt("health") != unit2.getInt("health")) {
				return false;
			}
			if (!unit1.getString("direction").equals(unit2.getString("direction"))) {
				return false;
			}
			if (!unit1.getString("state").equals(unit2.getString("state"))) {
				return false;
			}
			if (unit1.getDouble("xpos") != unit2.getDouble("xpos")) {
				return false;
			}
			if (unit1.getDouble("ypos") != unit2.getDouble("ypos")) {
				return false;
			}
		}
		
		// Check projectile
		
		boolean hasProjectile1 = frame.has("projectile");
		boolean hasProjectile2 = latestFrame.has("projectile");
		if (hasProjectile1 != hasProjectile2) {
			return false;
		}
		if (hasProjectile1) {
			JSONObject proj1 = frame.getJSONObject("projectile");
			JSONObject proj2 = latestFrame.getJSONObject("projectile");
			if (proj1.getInt("xpos") != proj2.getInt("xpos")) {
				return false;
			}
			if (proj1.getInt("ypos") != proj2.getInt("ypos")) {
				return false;
			}
		}
		
		// Check explosion
		
		boolean hasExplosion1 = frame.has("explosion");
		boolean hasExplosion2 = latestFrame.has("explosion");
		if (hasExplosion1 != hasExplosion2) {
			return false;
		}
		if (hasExplosion1) {
			JSONObject exp1 = frame.getJSONObject("explosion");
			JSONObject exp2 = latestFrame.getJSONObject("explosion");
			if (exp1.getInt("xpos") != exp2.getInt("xpos")) {
				return false;
			}
			if (exp1.getInt("ypos") != exp2.getInt("ypos")) {
				return false;
			}
			if (exp1.getInt("radius") != exp2.getInt("radius")) {
				return false;
			}
		}
		
		return true;
	}
}
