package edu.cornell.slicktest;

import java.util.ArrayList;

import org.newdawn.slick.SpriteSheet;

public class Item {
	protected String name;
	protected double basePrice;
	protected AttributeSet attributeBoosts;
	protected ArrayList<Ability> abilities;
	protected int equipCost;
	protected SpriteSheet spriteSheet;
	
	public Item(String name, double basePrice, AttributeSet attributeBoosts,
			ArrayList<Ability> abilities, int equipCost, SpriteSheet spriteSheet) {
		this.name = name;
		this.basePrice = basePrice;
		this.attributeBoosts = attributeBoosts;
		this.abilities = abilities;
		this.equipCost = equipCost;
		this.spriteSheet = spriteSheet;
	}
	
	
}
