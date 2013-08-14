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
			int damage, String spritesheetPath, boolean canjump, boolean canfly) {
		super();
		this.health = health;
		this.speed = speed;
		this.energyPerMove = energyPerMove;
		this.range = range;
		this.damage = damage;
		this.spritesheetPath = spritesheetPath;
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
		spritesheetPath =  attribte.spritesheetPath; 
		canjump = attribte.canjump; 
		canfly =  attribte.canfly;
		isAlive = attribte.isAlive;
	}
	
	public void decreaseAttributes(AttributeSet attribte) {
		health -= attribte.health; 
		speed -=  attribte.speed; 
		energyPerMove -=  attribte.energyPerMove; 
		range -=  attribte.range; 
		damage -=  attribte.damage; 
		spritesheetPath =  attribte.spritesheetPath; 
		canjump = attribte.canjump; 
		canfly =  attribte.canfly;
		isAlive = attribte.isAlive;
	}
}