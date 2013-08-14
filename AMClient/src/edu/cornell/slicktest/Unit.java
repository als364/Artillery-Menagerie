package edu.cornell.slicktest;
import java.awt.Point;
import java.util.ArrayList;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

import edu.cornell.slicktest.Enums.*;

public class Unit 
{
	
	protected String name;
	protected float width;
	protected float height;
	protected String displayName;
	protected String unitID;
	protected Units factoryKey;
	private AttributeSet defaultAttributes;
	protected AttributeSet equippedAttributes;
	protected ArrayList<Item> equipments;
	protected ArrayList<Ability> abilities;
	protected int equipSlotCount;
	protected float scale;
	protected final int maxSlotCount = 3;
	private final int jumpMultiplier = 2;
	private final int flyMultiplier = 3;
	private Point launchPoint;
	public int gravity;
	protected Body body;
	protected boolean done;
	protected boolean inuse;
	protected boolean dirty;
	
	private BattleStates battleStates;
	
	public Unit(){
		
	}
	
	public Unit(String name, String displayName, String unitID, Units UnitType, AttributeSet attrs, Point launchPoint, float scale, float width, float height)
	{
		this.name = name;
		this.displayName = displayName;
		this.unitID = unitID;
		this.scale = scale;
		this.width=width;
		this.height=height;
		this.launchPoint = launchPoint;
		factoryKey = UnitType;
		defaultAttributes = attrs;
		equippedAttributes = new AttributeSet(attrs);
		equipments = new ArrayList<Item>();
		abilities = new ArrayList<Ability>();
		equipSlotCount = maxSlotCount;
		gravity = 0;
		done = false;
	
	}
	
	
	// Equipment Handling Methods
	
	public boolean equipItem(Item item) {
		if (item.equipped || equipments.contains(item)) {
			return false;
		}
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
				item.equipped = true;
				return true;
			}
		}
		else {
			equipments.add(item);
			item.equipped = true;
			return true;
		}
		return false;
	}
	
	public boolean removeItem(Item item) {
		if (!equipments.contains(item) || !item.equipped) {
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
		item.equipped = false;
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
	//cost for Base ability defined in Factory.getAbility
	
	public Ability getBaseAbility() {
		if (abilities.size() > 0) {
			return abilities.get(0);
		}
		else {
			return null;
		}
	}
	
	public int getAbilityDamage(Ability ability) {
		return ability.getPower() + battleStates.currentAttributes.damage;
	}
	
	
	public Point getLaunchPoint() {
		if (battleStates.direction == Direction.RIGHT) {
			double width = battleStates.rectangle.getWidth();
			double distanceFromCenter = width/2 + (double)launchPoint.x;
			int x = launchPoint.x + (int)(2*distanceFromCenter);
			return new Point(x, launchPoint.y);
		}
		else {
			return launchPoint;
		}
	}
	
	// Battle States Handling Methods
	
	public void initBattleStates(UnitState initialState, Direction initialDirection, Rectangle initialRectangle) {
		battleStates = new BattleStates(equippedAttributes, initialState, initialDirection, initialRectangle);
	}
	
	public void toggleDone()
	{
		done = !done;
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
	
	public void setHealth(int health)
	{
		battleStates.currentAttributes.health = health;
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
		dirty = object.getBoolean("dirty");
	}
	
	// Creating a JSON Object containing the unit state information
	public JSONObject createJSONStateObject() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("unitID", unitID);
		object.put("health", battleStates.currentAttributes.health);
		object.put("direction", battleStates.direction.toString());
		object.put("state", battleStates.state.toString());
		object.put("xpos", body.getPosition().x);
		object.put("ypos", body.getPosition().y);
		object.put("dirty", dirty);
		return object;
	}
	
	public JSONObject createJSONCreationObject() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("displayName", displayName);
		object.put("unitID", unitID);
		object.put("factoryKey", factoryKey.toString());
		JSONArray itemArray = new JSONArray();
		for (Item item : equipments) {
			itemArray.put(item.itemID);
		}
		object.put("items", itemArray);
		return object;
	}
	
	public void setDirt(boolean dirty)
	{
		this.dirty = dirty;
	}
	
	public boolean getDirt()
	{
		return this.dirty;
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
	
	// Get a description of unit's current stats and abilities
	public String getDescription() {
		String out = "";
		out += "Equipment Energy: " + equipSlotCount + "\n";
		out += "Health: " + equippedAttributes.health + "\n";
		out += "Speed: " + equippedAttributes.speed + "\n";
		out += "Move Cost: " + equippedAttributes.energyPerMove + "\n";
		out += "Range: " + equippedAttributes.range + "\n";
		out += "Damage: " + equippedAttributes.damage + "\n";
		out += "Mobility: ";
		if (equippedAttributes.canjump && equippedAttributes.canfly) {
			out += "jump & fly\n";
		}
		else if (equippedAttributes.canjump) {
			out += "jump\n";
		}
		else if (equippedAttributes.canfly) {
			out += "fly\n";
		}
		else {
			out += "none\n";
		}
		out += "Abilities:" + "\n";
		for (Ability ability : abilities) {
			out += " - " + ability.getName() + "\n";
		}
		return out;
 	}
}
