package edu.cornell.slicktest;

import org.newdawn.slick.SpriteSheet;

public class AttributeSet
{
	protected int health;
	protected int speed;
	protected int energyPerMove;
	protected int range;
	protected int damage;
	protected String spritesheetPath;
	protected String enemySpritesheetPath;
	protected boolean canjump;
	protected boolean canfly;
	protected boolean isAlive;

	public AttributeSet()
	{
		health = 0;
		speed = 0;
		energyPerMove = 0;
		range = 0;
		damage = 0;
		spritesheetPath = "";
		canjump = false;
		canfly = false;
		isAlive = true;
	}
	
	public AttributeSet(int health, int speed, int energyPerMove, int range,
			int damage, String spritesheetPath, String enemySpritesheetPath, boolean canjump, boolean canfly) {
		super();
		this.health = health;
		this.speed = speed;
		this.energyPerMove = energyPerMove;
		this.range = range;
		this.damage = damage;
		this.spritesheetPath = spritesheetPath;
		this.enemySpritesheetPath = enemySpritesheetPath;
		this.canjump = canjump;
		this.canfly = canfly;
		isAlive = true;
	}
	
	public AttributeSet(AttributeSet attribte)
	{
		health = attribte.health; 
		speed =  attribte.speed; 
		energyPerMove =  attribte.energyPerMove; 
		range =  attribte.range; 
		damage =  attribte.damage; 
		spritesheetPath =  attribte.spritesheetPath; 
		enemySpritesheetPath = attribte.enemySpritesheetPath;
		canjump = attribte.canjump; 
		canfly =  attribte.canfly;
		isAlive = attribte.isAlive;
	}
	
	public void setAttributes(AttributeSet attribte)
	{
		health = attribte.health; 
		speed =  attribte.speed; 
		energyPerMove =  attribte.energyPerMove; 
		range =  attribte.range; 
		damage =  attribte.damage; 
		spritesheetPath =  attribte.spritesheetPath;
		enemySpritesheetPath = attribte.enemySpritesheetPath;
		canjump = attribte.canjump; 
		canfly =  attribte.canfly;
		isAlive = attribte.isAlive;
	}
	
	public void increaseAttributes(AttributeSet attribte) {
		health += attribte.health; 
		speed +=  attribte.speed; 
		energyPerMove +=  attribte.energyPerMove; 
		range +=  attribte.range; 
		damage +=  attribte.damage; 
		if (attribte.canjump) {
			canjump = true;
		}
		if (attribte.canfly) {
			canfly = true;
		}
	}
	
	public void decreaseAttributes(AttributeSet attribte) {
		health -= attribte.health; 
		speed -=  attribte.speed; 
		energyPerMove -=  attribte.energyPerMove; 
		range -=  attribte.range; 
		damage -=  attribte.damage; 
		if (!canjump) {
			canjump = false; 
		}
		if (!canfly) {
			canfly = false; 
		}
		if (!isAlive) {
			isAlive = false; 
		}
	}
	
	public String getBoostStr() {
		String out = "";
		if (health > 0) {
			out += " - health +" + health + "\n";
 		}
		else if (health < 0) {
			out += " - health -" + health + "\n";
		}
		if (speed > 0) {
			out += " - speed +" + speed + "\n";
		}
		else if (speed < 0){
			out += " - speed -" + speed + "\n";
		}
		if (energyPerMove > 0) {
		
			out += " - energy cost +" + energyPerMove + "\n";
		}
		else if (energyPerMove < 0){
			out += " - energy cost -" + energyPerMove + "\n";
		}
		if (range > 0) {
			out += " - range +" + range + "\n";
		}
		else if (range < 0){
			out += " - range -" + range + "\n";
		}
		if (damage > 0) {
			out += " - damage +" + damage + "\n";
		}
		else if (damage < 0){
			out += " - damage -" + damage + "\n";
		}
		if (canjump) {
			out += " - jump" + "\n";
		}
		if (canfly) {
			out += " - canfly" + "\n";
		}
		return out;
	}
}