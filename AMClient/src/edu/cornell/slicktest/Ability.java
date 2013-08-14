package edu.cornell.slicktest;

import org.newdawn.slick.SpriteSheet;

import edu.cornell.slicktest.Enums.Abilities;
import edu.cornell.slicktest.Enums.AbilityType;

public class Ability {
	
	
	
	
	private String name;
	private Abilities factoryKey;
	

	private AbilityType type;
	private SpriteSheet spriteSheet;
	private AttributeSet statusEffects;
	private int energyCost;
	private int range;
	private int power;
	
	public Ability(String name, Abilities abilityType, AbilityType type, SpriteSheet spriteSheet, int energyCost, int range, int power) {
		this.name = name;
		this.factoryKey = abilityType;
		this.type = type;
		this.spriteSheet = spriteSheet;
		this.energyCost = energyCost;
		this.range = range;
		this.power = power;
	}
	
	public boolean requireTarget() {
		return type == AbilityType.PROJECTILE_DAMAGE || type == AbilityType.PROJECTILE_HEAL;
	}
	
	public boolean useAbility(Unit user) {
		if (type == AbilityType.PROJECTILE_DAMAGE) {
			return true;
		}
		else if (type == AbilityType.PROJECTILE_HEAL) {
			return true;
		}
		else if (type == AbilityType.HEAL) {
			user.increaseHealth(power);
			return true;
		}
		else if (type == AbilityType.TRAP) {
			return true;
		}
		return false;
	}
	
	public boolean targetUnit(Unit target) {
		if (type == AbilityType.PROJECTILE_DAMAGE) {
			target.decreaseHealth(power);
			return true;
		}
		else if (type == AbilityType.PROJECTILE_HEAL) {
			target.increaseHealth(power);
			return true;
		}
		return false;
	}
	
	public String getName() {
		return name;
	}

	public AbilityType getType() {
		return type;
	}

	public SpriteSheet getSpriteSheet() {
		return spriteSheet;
	}

	public AttributeSet getStatusEffects() {
		return statusEffects;
	}

	public int getEnergyCost() {
		return energyCost;
	}

	public int getRange() {
		return range;
	}

	public int getPower() {
		return power;
	}
	
	public Abilities getFactoryKey() {
		return factoryKey;
	}
}
