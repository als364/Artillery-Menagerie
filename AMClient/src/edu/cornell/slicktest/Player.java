package edu.cornell.slicktest;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.newdawn.slick.SlickException;

import edu.cornell.slicktest.Enums.Items;
import edu.cornell.slicktest.Enums.Units;

public class Player {
	
	private static Player instance = null;
	
	protected String userID;
	protected String name;
	protected int money;
	protected ArrayList<Unit> units;
	protected ArrayList<Item> items;
	protected Army army;
	protected int rank;
	
	private Player() {
		userID = "Player";
		money = 0;
		units = new ArrayList<Unit>();
		items = new ArrayList<Item>();
		army = new Army(new ArrayList<Unit>(), Army.baseMaxEnergy);
		rank = 0;
	}
	
	public static Player getInstance() {
		if (instance == null) {
			instance = new Player();
		}
		return instance;
	}
	
	public boolean removeUnit(Unit unit) {
		if (units.contains(unit)) {
			units.remove(units);
			if (army.units.contains(unit)) {
				army.units.remove(unit);
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	public JSONObject createJSONObject() throws JSONException {
		JSONObject object = new JSONObject();
		
		object.put("userID", userID);
		
		// Units
		JSONArray unitArray = new JSONArray();
		for (Unit unit : units) {
			unitArray.put(unit.createJSONCreationObject());
		}
		object.put("units", unitArray);
		
		// Items
		JSONArray itemArray = new JSONArray();
		for (Item item : items) {
			itemArray.put(item.createJSONObject());
		}
		object.put("items", itemArray);
		
		// Army
		JSONArray armyArray = new JSONArray();
		for (Unit unit : army.units) {
			armyArray.put(unit.unitID);
		}
		object.put("army", armyArray);
		
		object.put("gold", money);
		object.put("rank", rank);
		
		return object;
	}
	
	public boolean loadJSONObject(JSONObject object) throws JSONException, SlickException {
		
		System.out.println(object);
		
		name = object.getString("name");
		
		if (object.getBoolean("newAccount")) {
			return true;
		}
		
		JSONArray itemArray = new JSONArray(object.getString("items"));
		items.clear();
		for (int i = 0; i < itemArray.length(); i++) {
			JSONObject itemObject = itemArray.getJSONObject(i);
			Item item = Factory.getItem(Items.valueOf(itemObject.getString("factoryKey")), itemObject.getString("itemID"));
			items.add(item);
		}
		
		JSONArray unitArray = new JSONArray(object.getString("units"));
		units.clear();
		for (int j = 0; j < unitArray.length(); j++) {
			JSONObject unitObject = unitArray.getJSONObject(j);
			Unit unit = Factory.getUnit(Units.valueOf(unitObject.getString("factoryKey")), unitObject.getString("displayName"), unitObject.getString("unitID"));
			JSONArray itemArray1 = unitObject.getJSONArray("items");
			for (int k = 0; k < itemArray1.length(); k++) {
				String itemID = itemArray1.getString(k);
				for (Item item : items) {
					if (itemID.equals(item.itemID)) {
						unit.equipItem(item);
						break;
					}
				}
			}
			units.add(unit);
		}
		
		JSONArray armyArray = new JSONArray(object.getString("army"));
		army.units.clear();
		for (int i = 0; i < armyArray.length(); i++) {
			String unitID = armyArray.getString(i);
			for (Unit unit : units) {
				if (unitID.equals(unit.unitID)) {
					army.units.add(unit);
					break;
				}
			}
		}
		
		money = object.getInt("gold");
		rank = object.getInt("rank");
		
		return false;
	}
	
	public void newAccountResouces() throws SlickException {
		//Units[] allUnitTypes = Units.values();
		Units[] allUnitTypes = {Units.SPACEMARINE, Units.UNICORN, Units.FAIRY, Units.CENTAUR};
		
		Units chosenUnitType;
		Unit unit;
		for (int i = 0; i < 4; i++) {
			chosenUnitType = allUnitTypes[(int)(Math.random()*allUnitTypes.length)];
			//System.out.println(chosenUnitType.toString());
			unit = Factory.getUnit(chosenUnitType, chosenUnitType.toString(), Math.random()+"");
			units.add(unit);
			army.units.add(unit);
		}
		
		Item item1 = Factory.getItem(Items.ROCKET_LAUNCHER, Math.random()+"");
		Item item2 = Factory.getItem(Items.ROCKET_LAUNCHER, Math.random()+"");
		Item item3 = Factory.getItem(Items.ROCKET_LAUNCHER, Math.random()+"");
		Item item4 = Factory.getItem(Items.ROCKET_LAUNCHER, Math.random()+"");
		Item item5 = Factory.getItem(Items.ROCKET_LAUNCHER, Math.random()+"");
		items.add(item1);
		items.add(item2);
		items.add(item3);
		items.add(item4);
		items.add(item5);
		money = 100;
		rank = 1000;
    }
}