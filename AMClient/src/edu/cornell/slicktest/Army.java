package edu.cornell.slicktest;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import edu.cornell.slicktest.Enums.UnitState;

public class Army
{
	public static final int MAX_SIZE = 4;
	public static int baseMaxEnergy = 200;
	
	protected ArrayList<Unit> units;
	protected ArrayList<Boolean> done;
	protected int maxHealth;
	protected int currentHealth;
	protected int maxEnergy;
	protected int currentEnergy;
	private int side;
	//protected Image armySymbol;
	protected Color armyColor;
	
	public Army(){
		
	}
	
	public Army(ArrayList<Unit> unitlist, int energyPool)
	{
		units = unitlist;
		maxHealth = 0;
		currentHealth = 0;
		done = new ArrayList<Boolean>();
		for(int i = 0; i < units.size(); i++)
		{
			maxHealth += units.get(i).getMaxHealth();
			done.add(false);
		}
		currentHealth += maxHealth;
		maxEnergy = energyPool;
	}
	
	public void setSide(int side) throws SlickException {
		this.side = side;
		if (side == 0) {
			armyColor = Color.green ;
		}
		else if (side == 1) {
			armyColor = Color.red;
		}
	}
	
	public int getSide() {
		return side;
	}
	
	public void refreshEnergy() 
	{
		currentEnergy = maxEnergy;
		
		for (int i = 0; i < done.size(); i++) {
			done.set(i, false);
		}
	}
	
	public void refreshState() {
		for (Unit unit : units) {
			unit.setUnitState(UnitState.STAND);
		}
	}
	
	public boolean isLost() {
		return units.size() == 0;
	}
	
	public void setEnergy(int energy)
	{
		currentEnergy = energy;
	}
	
	public Army clone(){
		Army toReturn = new Army();
		
		
		
		return toReturn;
	}
	
	public JSONObject createArmyJSON() throws JSONException {
		JSONObject armyObject = new JSONObject();
		JSONArray unitArray = new JSONArray();
		for (Unit unit : units) {
			JSONObject unitObject = new JSONObject();
			unitObject.put("displayName", unit.displayName);
			unitObject.put("unitID", unit.unitID);
			unitObject.put("factoryKey", unit.factoryKey.toString());
			JSONArray itemArray = new JSONArray();
			for (Item item : unit.equipments) {
				itemArray.put(item.factoryKey.toString());
			}
			unitObject.put("items", itemArray);
			unitArray.put(unitObject);
		}
		armyObject.put("units", unitArray);
		armyObject.put("energyPool", maxEnergy);
		return armyObject;
	}
}
