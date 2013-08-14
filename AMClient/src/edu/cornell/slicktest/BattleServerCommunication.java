package edu.cornell.slicktest;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import org.jbox2d.common.Vec2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import edu.cornell.slicktest.Enums.Items;
import edu.cornell.slicktest.Enums.Units;

public class BattleServerCommunication {

	public int loadWaitCount;
	public final int maxLoadWaitCount = 500;
	private JSONArray frameList;
	private JSONArray explosionList;
	private int mementoCount = 0;
	
	public BattleServerCommunication() {
		frameList = new JSONArray();
		explosionList = new JSONArray();
		loadWaitCount = 0;
	}
	
	public BattleJoinResult startBattle(String player, JSONObject army) throws IOException, JSONException, InterruptedException, SlickException {
		//System.out.println(player);
		String battleKey = ServerConnection.getBattle();
		int count = 0;
		while (true) {
			JSONObject join = ServerConnection.joinBattle(battleKey, player, army.toString());
			if (join.getBoolean("battleFound")) {
				if (join.getBoolean("joined")) {
					int playerNumber = join.getInt("number");
					System.out.println("Join: " + join);
					
					JSONObject armyInfo = null;
					while (true) {
						armyInfo = ServerConnection.getArmies(battleKey, playerNumber);
						if (armyInfo.getBoolean("complete")) {
							break;
						}
						Thread.sleep(100);
					}
					ArrayList<Army> armies = generateArmies(armyInfo.getJSONArray("armies"));
					return new BattleJoinResult(battleKey, playerNumber, armies);
				}
				else {
					count++;
					battleKey = ServerConnection.getBattle(count);
				}
			}
			else {
				Thread.sleep(100);
			}
		}
	}
	
	public void storeStateToServer(String battleKey, boolean battleEnded, int winner, int turn, int playerNumber, ArrayList<Army> armies) throws JSONException, IOException {
		JSONObject object = new JSONObject();
		object.put("battleKey", battleKey);
		object.put("finished", battleEnded);
		object.put("winner", winner);
		object.put("turn", turn);
		object.put("player", playerNumber);
		
		//byte[] frameListBytes = Compressor.compress(frameList.toString());
		object.put("frames", frameList);
		object.put("explosionList", explosionList);
		
		System.out.println("Store Battle: " + object.getString("battleKey"));
		System.out.println("Store: " + object);
		ServerConnection.storeBattle(object);
	}
	
	public BattleJoinResult resumeBattle(String battleKey) throws IOException, JSONException, InterruptedException, SlickException {
		
		JSONObject object = ServerConnection.resumeBattle(battleKey, Player.getInstance().userID);
		
		System.out.println("Resume Battle: " + object);
		
		ArrayList<Army> armies = generateArmies(object.getJSONArray("armies"));
		return new BattleJoinResult(object.getString("battleKey"), object.getInt("playerNumber"), armies, object.getBoolean("isFirstTurn"));
		
	}
	
	public BattleStateUpdate loadStateFromServer(String battleKey, int turn, ArrayList<Army> armies, 
			ArrayList<ArrayList<UnitAnimation>> unitAnimations, Physics physics, MSTerrain _terrain) throws Exception {
		
		JSONObject object = ServerConnection.loadBattle(battleKey, turn);
		
		//System.out.println("Load: " + object);
		
		BattleStateUpdate update = new BattleStateUpdate();
		if (object.getBoolean("needUpdate")) {
			String battleStateStr = object.getString("battleState");
			JSONObject battleState = new JSONObject(battleStateStr);
			update.turnCount = battleState.getInt("turn");
			update.battleEnded = battleState.getBoolean("finished");
			if (battleState.has("winner")) {
				update.winner = battleState.getInt("winner");
			}
			
			//byte[] frameArrayBytes = (byte[]) battleState.get("frames");
			//JSONArray frameArray = new JSONArray(Compressor.decompress(frameArrayBytes));
			JSONArray frameArray = battleState.getJSONArray("frames");
			
			update.framesToBeAnimated = frameArray;
			update.needUpdate = true;
			explosionList = battleState.getJSONArray("explosionList");
		}
		else {
			update.needUpdate = false;
		}
		return update;
	}
	
	public JSONArray getExplosionList() {
		return explosionList;
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
	
	public void addExplosion(double radius, int xpos, int ypos, int turn, int momentoCount) throws JSONException {
		
		//System.out.println("Server add shot: " + momentoCount);
		
		JSONObject object = new JSONObject();
		object.put("radius", radius);
		object.put("xpos", xpos);
		object.put("ypos", ypos);
		object.put("turn", turn);
		object.put("momentoCount", momentoCount);
		if (frameList.length() != 0) { 
			JSONObject latestFrame = frameList.getJSONObject(frameList.length()-1);
			latestFrame.put("explosion", object);
			explosionList.put(object);
		}
	}
	
	public void addShot(Unit shooter, int skillNumber) throws JSONException {
		JSONObject object = new JSONObject();
		object.put("unitID", shooter.unitID);
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
		mementoCount = 0;
	}
	
	public void storeFrame(ArrayList<Army> armies, int screenOffsetX, int screenOffsetY, Physics physics, int momentoCount) throws JSONException {
		JSONObject object = new JSONObject();
		
		object.put("momentoCount", momentoCount);
		
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
		
		// Store screen offset
		object.put("screenOffsetX", screenOffsetX);
		object.put("screenOffsetY", screenOffsetY);
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
	
	private ArrayList<Army> generateArmies(JSONArray armyArray) throws JSONException, SlickException {
		ArrayList<Army> armies = new ArrayList<Army>();
		for (int i = 0; i < armyArray.length(); i++) {
			JSONObject armyObject = armyArray.getJSONObject(i);
			JSONArray unitArray = armyObject.getJSONArray("units");
			ArrayList<Unit> units = new ArrayList<Unit>();
			for (int j = 0; j < unitArray.length() && j < Army.MAX_SIZE; j++) {
				JSONObject unitObject = unitArray.getJSONObject(j);
				Unit unit = Factory.getUnit(Units.valueOf(unitObject.getString("factoryKey")), unitObject.getString("displayName"), unitObject.getString("unitID"));
				JSONArray itemArray = unitObject.getJSONArray("items");
				for (int k = 0; k < itemArray.length(); k++) {
					String itemFactoryKey = itemArray.getString(k);
					Item item = Factory.getItem(Items.valueOf(itemFactoryKey), Math.random()+"");
					unit.equipItem(item);
				}
				units.add(unit);
			}
			Army army = new Army(units, armyObject.getInt("energyPool"));
			armies.add(army);
		}
		return armies;
	}
	
	public void restartTurn(int turn) throws JSONException {
		frameList = new JSONArray();
		for (int i = 0; i < explosionList.length(); i++) {
			if (explosionList.getJSONObject(i).getInt("turn") == turn) {
				explosionList.remove(i);
				i--;
			}
		}
	}
	
	public void unapplyMemento(int mementoCount) throws JSONException {
		//System.out.println("Serve unapply: " + mementoCount);
		if (frameList.length() != 0 ) {
			int lastMomentumCount = frameList.getJSONObject(frameList.length()-1).getInt("momentoCount");
			//System.out.println("Serve unapply2: " + lastMomentumCount);
			for (int i = 0; i < frameList.length(); i++) {
				if (frameList.getJSONObject(i).getInt("momentoCount") >= mementoCount) {
					frameList.remove(i);
					i--;
				}
			}
		}
		if (explosionList.length() != 0) {
			int lastMomentumCount = explosionList.getJSONObject(explosionList.length()-1).getInt("momentoCount");
			for (int i = 0; i < explosionList.length(); i++) {
				if (explosionList.getJSONObject(i).getInt("momentoCount") >= mementoCount) {
					explosionList.remove(i);
					i--;
				}
			}
		}
	}
	
	public int getMementoCount() {
		mementoCount++;
		return mementoCount;
	}
}
