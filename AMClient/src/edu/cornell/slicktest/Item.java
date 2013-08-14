package edu.cornell.slicktest;

import java.awt.Point;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.newdawn.slick.SpriteSheet;

import edu.cornell.slicktest.Enums.Items;

public class Item {
	protected String name;
	protected String itemID;
	protected double basePrice;
	protected AttributeSet attributeBoosts;
	protected ArrayList<Ability> abilities;
	protected int equipCost;
	protected SpriteSheet spriteSheet;
	protected Point equipPoint;
	protected Items factoryKey;
	protected boolean equipped;
	
	public Item(String name, Items factoryKey, String itemID, double basePrice, AttributeSet attributeBoosts,
			ArrayList<Ability> abilities, int equipCost, SpriteSheet spriteSheet, Point equipPoint) {
		this.name = name;
		this.factoryKey = factoryKey;
		this.basePrice = basePrice;
		this.attributeBoosts = attributeBoosts;
		this.abilities = abilities;
		this.equipCost = equipCost;
		this.spriteSheet = spriteSheet;
		this.equipPoint = equipPoint;
		equipped = false;
		this.itemID = itemID;
	}
	
	public String getDescription() {
		String out = "";
		out += "Name: " + name + "\n";
		out += "Equip Cost: " + equipCost + "\n";
		if (attributeBoosts != null) {
			out += "Boosts: " + "\n";
			out += attributeBoosts.getBoostStr();
		}
		if (abilities != null) {
			out += "Abilities: " + "\n";
			for (Ability ability : abilities) {
				out += "\t" + ability.getName() + "\n";
			}
		}
		return out;
	}
	
	public JSONObject createJSONObject() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("factoryKey", factoryKey.toString());
		object.put("itemID", itemID);
		object.put("equipped", equipped);
		return object;
	}
}
