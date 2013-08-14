package edu.cornell.slicktest;
import java.util.ArrayList;

import net.phys2d.math.Vector2f;

import org.jbox2d.dynamics.Body;

import org.json.JSONException;
import org.json.JSONObject;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

import edu.cornell.slicktest.Enums.*;

public class Unit 
{
	
	protected String name;
	protected String displayName;
	protected Units unitType;
	private AttributeSet defaultAttributes;
	protected AttributeSet equippedAttributes;
	protected ArrayList<Item> equipments;
	protected ArrayList<Ability> abilities;
	protected int equipSlotCount;
	protected int scale;
	protected final int maxSlotCount = 3;
	private final int jumpMultiplier = 2;
	private final int flyMultiplier = 3;
	public int gravity;
	protected Body body;
	
	private BattleStates battleStates;
	
	public Unit(){
		
	}
	
	public Unit(String name, String displayName, Units UnitType, AttributeSet attrs, int scale)
	{
		this.name = name;
		this.displayName = displayName;
		this.scale = scale;
		unitType = UnitType;
		defaultAttributes = attrs;
		equippedAttributes = new AttributeSet(attrs);
		equipments = new ArrayList<Item>();
		abilities = new ArrayList<Ability>();
		equipSlotCount = maxSlotCount;
		gravity = 0;
	
	}
	
	
	// Equipment Handling Methods
	
	public boolean equipItem(Item item) {
		if (item.equipCost > 0) {
			if (equipSlotCount - item.equipCost >= 0) {
				equipments.add(item);
				equipSlotCount -= item.equipCost;
				if (item.attributeBoosts != null) {
					// apply boost
					updateEquipmentAttributes();
				}
				if (item.abilities != null) {
					abilities.addAll(item.abilities);
				}
				return true;
			}
		}
		else {
			equipments.add(item);
			return true;
		}
		return false;
	}
	
	public boolean removeItem(Item item) {
		if (!equipments.contains(item)) {
			return false;
		}
		equipments.remove(item);
		equipSlotCount += item.equipCost;
		if (item.attributeBoosts != null) {
			// remove boosts
			updateEquipmentAttributes();
		} 
		if (item.abilities != null) {
			abilities.removeAll(item.abilities);
		}
		return true;
	}
	
	public void clearEquipment() {
		equipments.clear();
		equipSlotCount = maxSlotCount;
		updateEquipmentAttributes();
	}
	
	public void updateEquipmentAttributes() {
		equippedAttributes.setAttributes(defaultAttributes);
		for (Item i : equipments) {
			equippedAttributes.increaseAttributes(i.attributeBoosts);
		}
	}
	
	// Ability Handling Methods
	
	public Ability getBaseAbility() {
		if (abilities.size() > 0) {
			return abilities.get(0);
		}
		else {
			return null;
		}
	}
	
	
	// Battle States Handling Methods
	
	public void initBattleStates(UnitState initialState, Direction initialDirection, Rectangle initialRectangle) {
		battleStates = new BattleStates(equippedAttributes, initialState, initialDirection, initialRectangle);
	}
	
	public void resetUnitState() {
		battleStates.state = UnitState.STAND;
	}
	
	public Rectangle getUnitRectangle() {
		return battleStates.rectangle;
	}
	
	public void setUnitPosition(float x, float y) {
		battleStates.rectangle.setX(x);
		battleStates.rectangle.setY(y);
	}
	
	public void setUnitPositionX(float x) {
		battleStates.rectangle.setX(x);
	}
	
	public void setUnitPositionY(float y) {
		battleStates.rectangle.setY(y);
	}
	
	public float getJumpPeek() {
		return battleStates.jumpPeek;
	}
	
	public void startJump() {
		battleStates.jumpPeek = battleStates.rectangle.getY() - 100;
	}
	
	public UnitState getUnitState() {
		return battleStates.state;
	}
	
	public void setUnitState(UnitState state) {
		battleStates.state = state;
	}
	
	public Direction getUnitDirection() {
		return battleStates.direction;
	}
	
	public void setUnitDirection(Direction direction) {
		battleStates.direction = direction;
	}
	
	public AttributeSet getCurrentAttributes() {
		return battleStates.currentAttributes;
	}
	
	public int getCurrentHealth() {
		return battleStates.currentAttributes.health;
	}
	
	public int getMaxHealth() {
		return equippedAttributes.health;
	}
	
	public void decreaseHealth(int damage) {
		System.out.println("Damaged");
		battleStates.currentAttributes.health -= damage;
		if (battleStates.currentAttributes.health < 0) {
			battleStates.currentAttributes.health = 0;
		}
		if (battleStates.currentAttributes.health == 0) {
			battleStates.currentAttributes.isAlive = false;
		}
	}	
	
	public void increaseHealth(int heal) {
		battleStates.currentAttributes.health += heal;
		if (battleStates.currentAttributes.health > equippedAttributes.health) {
			battleStates.currentAttributes.health = equippedAttributes.health;
		}
	}


	public void setBody(Body b) {
		body = b;		
	}
	
	public Body body(){
		return body;
	}
	
	// JSON loading
	public void loadStateFromJSON(JSONObject object) throws JSONException {
		battleStates.currentAttributes.health = object.getInt("health");
		battleStates.direction = Direction.valueOf(object.getString("direction"));
		battleStates.state = UnitState.valueOf(object.getString("state"));
		float x = (float) object.getDouble("xpos");
		float y = (float) object.getDouble("ypos");
		body.getPosition().set(x, y);
		body.resetMassData();
	}
	
	// Creating a JSON Object containing the unit state information
	public JSONObject createJSONStateObject() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("unitID", displayName);
		object.put("health", battleStates.currentAttributes.health);
		object.put("direction", battleStates.direction.toString());
		object.put("state", battleStates.state.toString());
		object.put("xpos", body.getPosition().x);
		object.put("ypos", body.getPosition().y);
		return object;
	}
	
	/*
	public Unit clone(){
		Unit toReturn = new Unit();
		
		toReturn.abilities = new ArrayList<Ability>();
		for(Ability ability : abilities){
			toReturn.abilities.add(ability);
		}
		toReturn.battleStates = battleStates.clone();
		toReturn.body = body.clone();
		return toReturn;
	}
	*/
}
